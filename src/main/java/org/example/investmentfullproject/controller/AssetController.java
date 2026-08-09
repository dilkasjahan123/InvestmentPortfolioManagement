package org.example.investmentfullproject.controller;

import jakarta.servlet.http.HttpSession;
import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.AssetType;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.service.AssetService;
import org.example.investmentfullproject.service.PortfolioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AssetController {

    private final AssetService assetService;
    private final PortfolioService portfolioService;

    public AssetController(
            AssetService assetService,
            PortfolioService portfolioService) {
        this.assetService = assetService;
        this.portfolioService = portfolioService;
    }

    // Asset management dashboard
    @GetMapping("/assets")
    public String assets(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        List<Asset> assets =
                loggedUser.getRole() == Role.INVESTOR
                        ? assetService.getAssetsByInvestor(loggedUser.getUserId())
                        : assetService.getAllAssets();

        Map<Integer, List<Asset>> assetsByPortfolio = new LinkedHashMap<>();
        Map<Integer, BigDecimal> portfolioCurrentValues = new LinkedHashMap<>();

        for (Asset asset : assets) {
            Integer portfolioId = asset.getPortfolio().getPortfolioId();

            assetsByPortfolio
                    .computeIfAbsent(portfolioId, key -> new ArrayList<>())
                    .add(asset);

            portfolioCurrentValues.merge(
                    portfolioId,
                    asset.getCurrentValue(),
                    BigDecimal::add);
        }

        BigDecimal totalInvested = assets.stream()
                .map(Asset::getInvestedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrentValue = assets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfitLoss =
                totalCurrentValue.subtract(totalInvested);

        long totalQuantity = assets.stream()
                .map(Asset::getQuantity)
                .filter(quantity -> quantity != null)
                .mapToLong(Integer::longValue)
                .sum();

        model.addAttribute("user", loggedUser);
        model.addAttribute("assets", assets);
        model.addAttribute("assetsByPortfolio", assetsByPortfolio);
        model.addAttribute("portfolioCurrentValues", portfolioCurrentValues);
        model.addAttribute("assetCount", assets.size());
        model.addAttribute("portfolioCount", assetsByPortfolio.size());
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalInvested", totalInvested);
        model.addAttribute("totalCurrentValue", totalCurrentValue);
        model.addAttribute("totalProfitLoss", totalProfitLoss);
        model.addAttribute(
                "profitable",
                totalProfitLoss.compareTo(BigDecimal.ZERO) >= 0);

        return "asset";
    }

    // Display asset creation form
    @GetMapping("/assets/add")
    public String addAssetPage(
            HttpSession session,
            Model model) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() != Role.ADMIN) {
            return "redirect:/assets";
        }

        Asset asset = new Asset();
        asset.setPortfolio(new Portfolio());

        prepareForm(
                model,
                loggedUser,
                asset,
                "/assets/save");

        return "asset-form";
    }

    // Save new asset
    @PostMapping("/assets/save")
    public String saveAsset(
            @ModelAttribute("asset") Asset asset,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() != Role.ADMIN) {
            return "redirect:/assets";
        }

        try {
            assetService.addAsset(asset);
            redirectAttributes.addFlashAttribute(
                    "success", "Asset created successfully.");
            return "redirect:/assets";
        } catch (RuntimeException exception) {
            model.addAttribute("error", exception.getMessage());
            prepareForm(
                    model,
                    loggedUser,
                    asset,
                    "/assets/save");
            return "asset-form";
        }
    }

    // Display asset update form
    @GetMapping("/assets/edit/{id}")
    public String editAssetPage(
            @PathVariable Integer id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() != Role.ADMIN) {
            return "redirect:/assets";
        }

        try {
            Asset asset = assetService.getActiveAssetById(id);

            prepareForm(
                    model,
                    loggedUser,
                    asset,
                    "/assets/update");

            return "asset-form";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "error", exception.getMessage());
            return "redirect:/assets";
        }
    }

    // Update existing asset
    @PostMapping("/assets/update")
    public String updateAsset(
            @ModelAttribute("asset") Asset asset,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() != Role.ADMIN) {
            return "redirect:/assets";
        }

        try {
            assetService.updateAsset(asset);
            redirectAttributes.addFlashAttribute(
                    "success", "Asset updated successfully.");
            return "redirect:/assets";
        } catch (RuntimeException exception) {
            model.addAttribute("error", exception.getMessage());
            prepareForm(
                    model,
                    loggedUser,
                    asset,
                    "/assets/update");
            return "asset-form";
        }
    }

    // Soft-delete asset
    @PostMapping("/assets/delete/{id}")
    public String deleteAsset(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = getLoggedUser(session);

        if (loggedUser == null) {
            return "redirect:/";
        }

        if (loggedUser.getRole() != Role.ADMIN) {
            return "redirect:/assets";
        }

        try {
            assetService.deleteAsset(id);
            redirectAttributes.addFlashAttribute(
                    "success", "Asset deleted successfully.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "error", exception.getMessage());
        }

        return "redirect:/assets";
    }

    // Populate shared form attributes
    private void prepareForm(
            Model model,
            User loggedUser,
            Asset asset,
            String formAction) {

        List<Portfolio> portfolios =
                new ArrayList<>(portfolioService.getAllPortfolios());

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

        model.addAttribute("user", loggedUser);
        model.addAttribute("asset", asset);
        model.addAttribute("formAction", formAction);
        model.addAttribute("assetTypes", AssetType.values());
        model.addAttribute("portfolios", portfolios);
    }

    // Retrieve logged-in user from session
    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
    }
}
