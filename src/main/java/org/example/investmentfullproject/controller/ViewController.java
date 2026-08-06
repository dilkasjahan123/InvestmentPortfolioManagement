package org.example.investmentfullproject.controller;

import jakarta.servlet.http.HttpSession;
import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.RiskLevel;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.service.AssetService;
import org.example.investmentfullproject.service.AuthService;
import org.example.investmentfullproject.service.PortfolioService;
import org.example.investmentfullproject.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class ViewController {

    private final AuthService authService;
    private final PortfolioService portfolioService;
    private final AssetService assetService;
    private final TransactionService transactionService;

    public ViewController(
            AuthService authService,
            PortfolioService portfolioService,
            AssetService assetService, TransactionService transactionService) {
        this.authService = authService;
        this.portfolioService = portfolioService;
        this.assetService = assetService;
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public String loginPage(
            @RequestParam(
                    value = "registered",
                    required = false)
            String registered,
            Model model) {

        model.addAttribute("user", new User());

        if (registered != null) {
            model.addAttribute(
                    "success",
                    "Registration Successful. Please Login.");
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/admin")
    public String adminPage(
            HttpSession session,
            Model model) {

        User user = getLoggedUser(session);

        if (user == null) {
            return "redirect:/";
        }

        if (user.getRole() != Role.ADMIN) {
            return "redirect:/" + user.getRole()
                    .name()
                    .toLowerCase(Locale.ENGLISH);
        }

        List<User> users = authService.getAllUsers();
        List<Portfolio> portfolios =
                portfolioService.getAllPortfolios();
        List<Asset> assets = assetService.getAllAssets();

        long adminCount = users.stream()
                .filter(account ->
                        account.getRole() == Role.ADMIN)
                .count();

        long advisorCount = users.stream()
                .filter(account ->
                        account.getRole() == Role.ADVISOR)
                .count();

        long investorCount = users.stream()
                .filter(account ->
                        account.getRole() == Role.INVESTOR)
                .count();

        BigDecimal totalInvested = assets.stream()
                .map(asset -> calculateValue(
                        asset.getPurchasePrice(),
                        asset.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrentValue = assets.stream()
                .map(asset -> calculateValue(
                        asset.getCurrentPrice(),
                        asset.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfitLoss =
                totalCurrentValue.subtract(totalInvested);

        model.addAttribute("user", user);
        model.addAttribute("userCount", users.size());
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("advisorCount", advisorCount);
        model.addAttribute("investorCount", investorCount);
        model.addAttribute("portfolioCount", portfolios.size());
        model.addAttribute("assetCount", assets.size());
        model.addAttribute("transactionCount", transactionService.getTransactionCount());
        model.addAttribute("totalInvested", totalInvested);
        model.addAttribute(
                "totalCurrentValue",
                totalCurrentValue);
        model.addAttribute(
                "totalProfitLoss",
                totalProfitLoss);
        model.addAttribute(
                "totalReturnPercentage",
                calculatePercentage(
                        totalProfitLoss,
                        totalInvested));
        model.addAttribute(
                "highRiskCount",
                countRisk(portfolios, RiskLevel.HIGH));
        model.addAttribute(
                "mediumRiskCount",
                countRisk(portfolios, RiskLevel.MEDIUM));
        model.addAttribute(
                "lowRiskCount",
                countRisk(portfolios, RiskLevel.LOW));
        model.addAttribute(
                "today",
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern(
                                "EEEE, dd MMMM yyyy",
                                Locale.ENGLISH)));

        return "admin";
    }

    @GetMapping("/advisor")
    public String advisorPage(
            HttpSession session,
            Model model) {

        User user = getLoggedUser(session);

        if (user == null) {
            return "redirect:/";
        }

        if (user.getRole() != Role.ADVISOR) {
            return "redirect:/";
        }

        List<User> investors = authService.getAllUsers()
                .stream()
                .filter(candidate ->
                        candidate.getRole() == Role.INVESTOR)
                .sorted(
                        Comparator.comparing(
                                User::getUsername,
                                Comparator.nullsLast(
                                        String.CASE_INSENSITIVE_ORDER)))
                .toList();

        List<Portfolio> portfolios =
                new ArrayList<>(
                        portfolioService.getAllPortfolios());

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

        List<Asset> assets =
                assetService.getAllAssets();

        Map<Integer, Integer> portfolioCountByInvestor =
                new LinkedHashMap<>();

        Map<Integer, Integer> assetCountByInvestor =
                new LinkedHashMap<>();

        Map<Integer, BigDecimal> investedValueByInvestor =
                new LinkedHashMap<>();

        Map<Integer, BigDecimal> currentValueByInvestor =
                new LinkedHashMap<>();

        Map<Integer, BigDecimal> profitLossByInvestor =
                new LinkedHashMap<>();

        for (User investor : investors) {
            Integer investorId = investor.getUserId();

            portfolioCountByInvestor.put(investorId, 0);
            assetCountByInvestor.put(investorId, 0);
            investedValueByInvestor.put(
                    investorId,
                    BigDecimal.ZERO);
            currentValueByInvestor.put(
                    investorId,
                    BigDecimal.ZERO);
            profitLossByInvestor.put(
                    investorId,
                    BigDecimal.ZERO);
        }

        for (Portfolio portfolio : portfolios) {
            if (portfolio.getInvestor() == null
                    || portfolio.getInvestor().getUserId() == null) {
                continue;
            }

            portfolioCountByInvestor.merge(
                    portfolio.getInvestor().getUserId(),
                    1,
                    Integer::sum);
        }

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (Asset asset : assets) {
            if (asset.getPortfolio() == null
                    || asset.getPortfolio().getInvestor() == null
                    || asset.getPortfolio()
                    .getInvestor().getUserId() == null) {
                continue;
            }

            Integer investorId = asset.getPortfolio()
                    .getInvestor()
                    .getUserId();

            BigDecimal investedValue =
                    calculateValue(
                            asset.getPurchasePrice(),
                            asset.getQuantity());

            BigDecimal currentValue =
                    calculateValue(
                            asset.getCurrentPrice(),
                            asset.getQuantity());

            BigDecimal profitLoss =
                    currentValue.subtract(investedValue);

            assetCountByInvestor.merge(
                    investorId,
                    1,
                    Integer::sum);

            investedValueByInvestor.merge(
                    investorId,
                    investedValue,
                    BigDecimal::add);

            currentValueByInvestor.merge(
                    investorId,
                    currentValue,
                    BigDecimal::add);

            profitLossByInvestor.merge(
                    investorId,
                    profitLoss,
                    BigDecimal::add);

            totalInvested =
                    totalInvested.add(investedValue);

            totalCurrentValue =
                    totalCurrentValue.add(currentValue);
        }

        BigDecimal totalProfitLoss =
                totalCurrentValue.subtract(totalInvested);

        BigDecimal totalReturnPercentage =
                calculatePercentage(
                        totalProfitLoss,
                        totalInvested);

        long highRiskCount =
                countRisk(portfolios, RiskLevel.HIGH);

        long mediumRiskCount =
                countRisk(portfolios, RiskLevel.MEDIUM);

        long lowRiskCount =
                countRisk(portfolios, RiskLevel.LOW);

        int portfolioCount = portfolios.size();

        model.addAttribute("user", user);
        model.addAttribute("investors", investors);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("assets", assets);
        model.addAttribute(
                "investorCount",
                investors.size());
        model.addAttribute(
                "portfolioCount",
                portfolioCount);
        model.addAttribute(
                "assetCount",
                assets.size());
        model.addAttribute(
                "totalInvested",
                totalInvested);
        model.addAttribute(
                "totalCurrentValue",
                totalCurrentValue);
        model.addAttribute(
                "totalProfitLoss",
                totalProfitLoss);
        model.addAttribute(
                "totalReturnPercentage",
                totalReturnPercentage);
        model.addAttribute(
                "profitable",
                totalProfitLoss.compareTo(
                        BigDecimal.ZERO) >= 0);
        model.addAttribute(
                "portfolioCountByInvestor",
                portfolioCountByInvestor);
        model.addAttribute(
                "assetCountByInvestor",
                assetCountByInvestor);
        model.addAttribute(
                "investedValueByInvestor",
                investedValueByInvestor);
        model.addAttribute(
                "currentValueByInvestor",
                currentValueByInvestor);
        model.addAttribute(
                "profitLossByInvestor",
                profitLossByInvestor);
        model.addAttribute(
                "highRiskCount",
                highRiskCount);
        model.addAttribute(
                "mediumRiskCount",
                mediumRiskCount);
        model.addAttribute(
                "lowRiskCount",
                lowRiskCount);
        model.addAttribute(
                "highRiskPercentage",
                calculateCountPercentage(
                        highRiskCount,
                        portfolioCount));
        model.addAttribute(
                "mediumRiskPercentage",
                calculateCountPercentage(
                        mediumRiskCount,
                        portfolioCount));
        model.addAttribute(
                "lowRiskPercentage",
                calculateCountPercentage(
                        lowRiskCount,
                        portfolioCount));
        model.addAttribute(
                "today",
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern(
                                "EEEE, dd MMMM yyyy",
                                Locale.ENGLISH)));

        return "advisor-dashboard";
    }

    @GetMapping("/investor")
    public String investorPage(
            HttpSession session,
            Model model) {

        User user = getLoggedUser(session);

        if (user == null) {
            return "redirect:/";
        }

        if (user.getRole() != Role.INVESTOR) {
            return "redirect:/";
        }

        List<Portfolio> portfolios = new ArrayList<>(
                portfolioService.getPortfoliosByInvestor(
                        user.getUserId()));

        portfolios.sort(
                Comparator.comparing(
                        Portfolio::getPortfolioName,
                        Comparator.nullsLast(
                                String.CASE_INSENSITIVE_ORDER)));

        List<Asset> assets =
                assetService.getAssetsByInvestor(
                        user.getUserId());

        Map<Integer, Integer> assetCountByPortfolio =
                new LinkedHashMap<>();

        Map<Integer, BigDecimal> investedByPortfolio =
                new LinkedHashMap<>();

        Map<Integer, BigDecimal> currentValueByPortfolio =
                new LinkedHashMap<>();

        Map<Integer, BigDecimal> profitLossByPortfolio =
                new LinkedHashMap<>();

        Map<Integer, BigDecimal> allocationByPortfolio =
                new LinkedHashMap<>();

        for (Portfolio portfolio : portfolios) {
            Integer portfolioId = portfolio.getPortfolioId();

            assetCountByPortfolio.put(portfolioId, 0);
            investedByPortfolio.put(
                    portfolioId,
                    BigDecimal.ZERO);
            currentValueByPortfolio.put(
                    portfolioId,
                    BigDecimal.ZERO);
            profitLossByPortfolio.put(
                    portfolioId,
                    BigDecimal.ZERO);
        }

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (Asset asset : assets) {
            if (asset.getPortfolio() == null
                    || asset.getPortfolio().getPortfolioId() == null) {
                continue;
            }

            Integer portfolioId =
                    asset.getPortfolio().getPortfolioId();

            BigDecimal investedValue =
                    calculateValue(
                            asset.getPurchasePrice(),
                            asset.getQuantity());

            BigDecimal currentValue =
                    calculateValue(
                            asset.getCurrentPrice(),
                            asset.getQuantity());

            BigDecimal profitLoss =
                    currentValue.subtract(investedValue);

            assetCountByPortfolio.merge(
                    portfolioId,
                    1,
                    Integer::sum);

            investedByPortfolio.merge(
                    portfolioId,
                    investedValue,
                    BigDecimal::add);

            currentValueByPortfolio.merge(
                    portfolioId,
                    currentValue,
                    BigDecimal::add);

            profitLossByPortfolio.merge(
                    portfolioId,
                    profitLoss,
                    BigDecimal::add);

            totalInvested =
                    totalInvested.add(investedValue);

            totalCurrentValue =
                    totalCurrentValue.add(currentValue);
        }

        BigDecimal totalProfitLoss =
                totalCurrentValue.subtract(totalInvested);

        BigDecimal totalReturnPercentage =
                calculatePercentage(
                        totalProfitLoss,
                        totalInvested);

        for (Portfolio portfolio : portfolios) {
            Integer portfolioId = portfolio.getPortfolioId();

            BigDecimal portfolioValue =
                    currentValueByPortfolio.getOrDefault(
                            portfolioId,
                            BigDecimal.ZERO);

            allocationByPortfolio.put(
                    portfolioId,
                    calculatePercentage(
                            portfolioValue,
                            totalCurrentValue));
        }

        long highRiskCount =
                countRisk(portfolios, RiskLevel.HIGH);

        long mediumRiskCount =
                countRisk(portfolios, RiskLevel.MEDIUM);

        long lowRiskCount =
                countRisk(portfolios, RiskLevel.LOW);

        int portfolioCount = portfolios.size();

        model.addAttribute("user", user);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("assets", assets);
        model.addAttribute(
                "portfolioCount",
                portfolioCount);
        model.addAttribute(
                "assetCount",
                assets.size());
        model.addAttribute(
                "totalInvested",
                totalInvested);
        model.addAttribute(
                "totalCurrentValue",
                totalCurrentValue);
        model.addAttribute(
                "totalProfitLoss",
                totalProfitLoss);
        model.addAttribute(
                "totalReturnPercentage",
                totalReturnPercentage);
        model.addAttribute(
                "profitable",
                totalProfitLoss.compareTo(
                        BigDecimal.ZERO) >= 0);
        model.addAttribute(
                "assetCountByPortfolio",
                assetCountByPortfolio);
        model.addAttribute(
                "investedByPortfolio",
                investedByPortfolio);
        model.addAttribute(
                "currentValueByPortfolio",
                currentValueByPortfolio);
        model.addAttribute(
                "profitLossByPortfolio",
                profitLossByPortfolio);
        model.addAttribute(
                "allocationByPortfolio",
                allocationByPortfolio);
        model.addAttribute(
                "highRiskCount",
                highRiskCount);
        model.addAttribute(
                "mediumRiskCount",
                mediumRiskCount);
        model.addAttribute(
                "lowRiskCount",
                lowRiskCount);
        model.addAttribute(
                "highRiskPercentage",
                calculateCountPercentage(
                        highRiskCount,
                        portfolioCount));
        model.addAttribute(
                "mediumRiskPercentage",
                calculateCountPercentage(
                        mediumRiskCount,
                        portfolioCount));
        model.addAttribute(
                "lowRiskPercentage",
                calculateCountPercentage(
                        lowRiskCount,
                        portfolioCount));
        model.addAttribute(
                "today",
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern(
                                "EEEE, dd MMMM yyyy",
                                Locale.ENGLISH)));

        return "investor-dashboard";
    }

    @GetMapping("/profile")
    public String profilePage(
            HttpSession session,
            Model model) {

        User user = getLoggedUser(session);

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @RequestMapping(
            value = "/logout",
            method = {
                    RequestMethod.GET,
                    RequestMethod.POST
            })
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/users")
    public String usersPage(
            HttpSession session,
            Model model) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() != Role.ADMIN) {
            return "redirect:/" + loggedUser.getRole()
                    .name()
                    .toLowerCase(Locale.ENGLISH);
        }

        List<User> users = authService.getAllUsers();

        model.addAttribute("user", loggedUser);
        model.addAttribute("users", users);
        model.addAttribute("userCount", users.size());

        long advisorCount = users.stream()
                .filter(user ->
                        user.getRole() == Role.ADVISOR)
                .count();

        long investorCount = users.stream()
                .filter(user ->
                        user.getRole() == Role.INVESTOR)
                .count();

        long adminCount = users.stream()
                .filter(user ->
                        user.getRole() == Role.ADMIN)
                .count();

        model.addAttribute(
                "advisorCount",
                advisorCount);
        model.addAttribute(
                "investorCount",
                investorCount);
        model.addAttribute(
                "adminCount",
                adminCount);

        return "users";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() != Role.ADMIN) {
            return "redirect:/" + loggedUser.getRole()
                    .name()
                    .toLowerCase(Locale.ENGLISH);
        }

        if (loggedUser.getUserId().equals(id)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "You cannot delete your own administrator account.");
            return "redirect:/users";
        }

        try {
            User account = authService.getUserProfile(id);

            if (account.getRole() == Role.ADMIN) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Administrator accounts are protected.");
                return "redirect:/users";
            }

            authService.deleteUser(id);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "User deleted successfully.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage());
        }

        return "redirect:/users";
    }

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
    }

    private BigDecimal calculateValue(
            BigDecimal price,
            Integer quantity) {

        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }

        return price.multiply(
                BigDecimal.valueOf(quantity));
    }

    private BigDecimal calculatePercentage(
            BigDecimal value,
            BigDecimal total) {

        if (total == null
                || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return value
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        total,
                        1,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCountPercentage(
            long count,
            int total) {

        if (total == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(total),
                        1,
                        RoundingMode.HALF_UP);
    }

    private long countRisk(
            List<Portfolio> portfolios,
            RiskLevel riskLevel) {

        return portfolios.stream()
                .filter(portfolio ->
                        portfolio.getRiskLevel() == riskLevel)
                .count();
    }
}
