package org.example.investmentfullproject.controller;

import jakarta.servlet.http.HttpSession;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.RiskLevel;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.service.AuthService;
import org.example.investmentfullproject.service.PortfolioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PortfolioViewController {

    private final PortfolioService portfolioService;
    private final AuthService authService;

    public PortfolioViewController(
            PortfolioService portfolioService,
            AuthService authService) {
        this.portfolioService = portfolioService;
        this.authService = authService;
    }

    // Portfolio management dashboard
    @GetMapping("/portfolios")
    public String portfolios(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        List<Portfolio> portfolios = new ArrayList<>(
                loggedUser.getRole() == Role.INVESTOR
                        ? portfolioService.getPortfoliosByInvestor(loggedUser.getUserId())
                        : portfolioService.getAllPortfolios());

        portfolios.sort(
                Comparator.comparing(
                                (Portfolio portfolio) ->
                                        portfolio.getInvestor() == null)
                        .thenComparing(
                                (Portfolio portfolio) ->
                                        portfolio.getInvestor() == null
                                                || portfolio.getInvestor().getUsername() == null
                                                ? ""
                                                : portfolio.getInvestor().getUsername(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(
                                portfolio -> portfolio.getInvestor() == null
                                        || portfolio.getInvestor().getUserId() == null
                                        ? Integer.MAX_VALUE
                                        : portfolio.getInvestor().getUserId())
                        .thenComparing(
                                Portfolio::getPortfolioName,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

        Map<Integer, List<Portfolio>> portfoliosByInvestor =
                new LinkedHashMap<>();

        for (Portfolio portfolio : portfolios) {
            Integer investorId =
                    portfolio.getInvestor() == null
                            ? null
                            : portfolio.getInvestor().getUserId();

            portfoliosByInvestor
                    .computeIfAbsent(investorId, key -> new ArrayList<>())
                    .add(portfolio);
        }

        model.addAttribute("user", loggedUser);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("portfoliosByInvestor", portfoliosByInvestor);
        model.addAttribute("portfolioCount", portfolios.size());
        model.addAttribute("highRiskCount", countByRisk(portfolios, RiskLevel.HIGH));
        model.addAttribute("mediumRiskCount", countByRisk(portfolios, RiskLevel.MEDIUM));
        model.addAttribute("lowRiskCount", countByRisk(portfolios, RiskLevel.LOW));

        return "portfolio";
    }

    // Display create portfolio form
    @GetMapping("/portfolio/add")
    public String addPortfolioPage(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() == Role.ADVISOR) {
            return "redirect:/portfolios";
        }

        Portfolio portfolio = new Portfolio();

        if (loggedUser.getRole() == Role.INVESTOR) {
            portfolio.setInvestor(loggedUser);
        } else {
            // Ensures that *{investor.userId} can be rendered for an admin.
            portfolio.setInvestor(new User());
        }

        prepareForm(model, loggedUser, portfolio, "/portfolio/save");
        return "portfolio-form";
    }

    // Save new portfolio
    @PostMapping("/portfolio/save")
    public String savePortfolio(
            @ModelAttribute("portfolio") Portfolio portfolio,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() == Role.ADVISOR) {
            return "redirect:/portfolios";
        }

        if (loggedUser.getRole() == Role.INVESTOR) {
            // Never trust a submitted investor ID for an investor account.
            portfolio.setInvestor(loggedUser);
        }

        try {
            portfolioService.createPortfolio(portfolio);
            redirectAttributes.addFlashAttribute(
                    "success", "Portfolio created successfully.");
            return "redirect:/portfolios";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/portfolio/add";
        }
    }

    // Display edit portfolio form
    @GetMapping("/portfolio/edit/{id}")
    public String editPortfolio(
            @PathVariable Integer id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() == Role.ADVISOR) {
            return "redirect:/portfolios";
        }

        try {
            Portfolio portfolio = portfolioService.getPortfolioById(id);

            if (!canModify(loggedUser, portfolio)) {
                redirectAttributes.addFlashAttribute(
                        "error", "You cannot update this portfolio.");
                return "redirect:/portfolios";
            }

            prepareForm(model, loggedUser, portfolio, "/portfolio/update");
            return "portfolio-form";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/portfolios";
        }
    }

    // Update portfolio
    @PostMapping("/portfolio/update")
    public String updatePortfolio(
            @ModelAttribute("portfolio") Portfolio submittedPortfolio,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() == Role.ADVISOR) {
            return "redirect:/portfolios";
        }

        try {
            Portfolio existingPortfolio =
                    portfolioService.getPortfolioById(submittedPortfolio.getPortfolioId());

            if (!canModify(loggedUser, existingPortfolio)) {
                redirectAttributes.addFlashAttribute(
                        "error", "You cannot update this portfolio.");
                return "redirect:/portfolios";
            }

            if (loggedUser.getRole() == Role.INVESTOR) {
                // Prevent an investor from transferring ownership through form tampering.
                submittedPortfolio.setInvestor(existingPortfolio.getInvestor());
            }

            portfolioService.updatePortfolio(submittedPortfolio);
            redirectAttributes.addFlashAttribute(
                    "success", "Portfolio updated successfully.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/portfolios";
    }

    // Delete portfolio
    @PostMapping("/portfolio/delete/{id}")
    public String deletePortfolio(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() == Role.ADVISOR) {
            return "redirect:/portfolios";
        }

        try {
            Portfolio portfolio = portfolioService.getPortfolioById(id);

            if (!canModify(loggedUser, portfolio)) {
                redirectAttributes.addFlashAttribute(
                        "error", "You cannot delete this portfolio.");
                return "redirect:/portfolios";
            }

            portfolioService.deletePortfolio(id);
            redirectAttributes.addFlashAttribute(
                    "success", "Portfolio deleted successfully.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/portfolios";
    }

    // Populate common form attributes
    private void prepareForm(
            Model model,
            User loggedUser,
            Portfolio portfolio,
            String formAction) {

        model.addAttribute("user", loggedUser);
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("formAction", formAction);
        model.addAttribute("riskLevels", Arrays.asList(RiskLevel.values()));
        model.addAttribute(
                "investors",
                authService.getAllUsers()
                        .stream()
                        .filter(user -> user.getRole() == Role.INVESTOR)
                        .toList());
    }

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
    }

    // Check whether user can modify portfolio
    private boolean canModify(User loggedUser, Portfolio portfolio) {
        return loggedUser.getRole() == Role.ADMIN
                || (loggedUser.getRole() == Role.INVESTOR
                && portfolio.getInvestor() != null
                && portfolio.getInvestor().getUserId().equals(loggedUser.getUserId()));
    }

    private long countByRisk(List<Portfolio> portfolios, RiskLevel riskLevel) {
        return portfolios.stream()
                .filter(portfolio -> portfolio.getRiskLevel() == riskLevel)
                .count();
    }
}
