package org.example.investmentfullproject.controller;

import jakarta.servlet.http.HttpSession;
import org.example.investmentfullproject.model.PerformanceReport;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.service.AnalyticsService;
import org.example.investmentfullproject.service.PortfolioAnalytics;
import org.example.investmentfullproject.service.PortfolioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class PerformanceReportViewController {

    private final AnalyticsService analyticsService;
    private final PortfolioService portfolioService;

    public PerformanceReportViewController(
            AnalyticsService analyticsService,
            PortfolioService portfolioService) {
        this.analyticsService = analyticsService;
        this.portfolioService = portfolioService;
    }

    @GetMapping({
            "/performance-report",
            "/performance-reports"
    })
    public String performanceReport(
            @RequestParam(required = false) Integer portfolioId,
            @RequestParam(required = false) Integer investorId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        List<Portfolio> portfolios =
                getAccessiblePortfolios(loggedUser, investorId);

        portfolios.sort(
                Comparator.comparing(
                                (Portfolio portfolio) ->
                                        portfolio.getInvestor() == null
                                                || portfolio.getInvestor()
                                                .getUsername() == null
                                                ? ""
                                                : portfolio.getInvestor()
                                                .getUsername(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(
                                Portfolio::getPortfolioName,
                                Comparator.nullsLast(
                                        String.CASE_INSENSITIVE_ORDER)));

        Portfolio selectedPortfolio = selectPortfolio(
                portfolios,
                portfolioId);

        if (portfolioId != null && selectedPortfolio == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "The selected portfolio is not available.");
            return "redirect:/performance-report";
        }

        model.addAttribute("user", loggedUser);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("selectedPortfolio", selectedPortfolio);
        model.addAttribute("investorId", investorId);

        if (selectedPortfolio != null) {
            PortfolioAnalytics analytics =
                    analyticsService.getAnalytics(
                            selectedPortfolio.getPortfolioId());

            List<PerformanceReport> reports =
                    analyticsService.getReportsByPortfolio(
                            selectedPortfolio.getPortfolioId());

            model.addAttribute("analytics", analytics);
            model.addAttribute("reports", reports);
            model.addAttribute(
                    "allocationLabels",
                    new ArrayList<>(
                            analytics.allocationByType().keySet()));
            model.addAttribute(
                    "allocationValues",
                    new ArrayList<>(
                            analytics.allocationByType().values()));
            model.addAttribute(
                    "historyLabels",
                    reports.stream()
                            .map(report ->
                                    report.getReportDate().toString())
                            .toList());
            model.addAttribute(
                    "historyValues",
                    reports.stream()
                            .map(PerformanceReport::getReturnPercentage)
                            .toList());
        }

        return "performance-report";
    }

    @PostMapping("/performance-report/generate/{portfolioId}")
    public String generateReport(
            @PathVariable Integer portfolioId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        Portfolio portfolio;

        try {
            portfolio = portfolioService.getPortfolioById(portfolioId);
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage());
            return "redirect:/performance-report";
        }

        if (!canAccess(loggedUser, portfolio)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "You cannot generate a report for this portfolio.");
            return "redirect:/performance-report";
        }

        analyticsService.generateReport(portfolioId);
        redirectAttributes.addFlashAttribute(
                "success",
                "Performance report generated successfully.");

        return "redirect:/performance-report?portfolioId=" + portfolioId;
    }

    private List<Portfolio> getAccessiblePortfolios(
            User loggedUser,
            Integer investorId) {

        if (loggedUser.getRole() == Role.INVESTOR) {
            return new ArrayList<>(
                    portfolioService.getPortfoliosByInvestor(
                            loggedUser.getUserId()));
        }

        List<Portfolio> portfolios =
                new ArrayList<>(portfolioService.getAllPortfolios());

        if (investorId != null) {
            portfolios.removeIf(portfolio ->
                    portfolio.getInvestor() == null
                            || !investorId.equals(
                            portfolio.getInvestor().getUserId()));
        }

        return portfolios;
    }

    private Portfolio selectPortfolio(
            List<Portfolio> portfolios,
            Integer portfolioId) {

        if (portfolioId == null) {
            return portfolios.stream().findFirst().orElse(null);
        }

        return portfolios.stream()
                .filter(portfolio ->
                        portfolioId.equals(portfolio.getPortfolioId()))
                .findFirst()
                .orElse(null);
    }

    private boolean canAccess(User user, Portfolio portfolio) {
        return user.getRole() != Role.INVESTOR
                || (portfolio.getInvestor() != null
                && user.getUserId().equals(
                portfolio.getInvestor().getUserId()));
    }

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
    }
}
