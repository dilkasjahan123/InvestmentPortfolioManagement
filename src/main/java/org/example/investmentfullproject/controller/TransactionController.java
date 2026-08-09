package org.example.investmentfullproject.controller;

import jakarta.servlet.http.HttpSession;
import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.Transaction;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.service.AssetService;
import org.example.investmentfullproject.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping({"/transactions", "/transaction"})
public class TransactionController {

    private final TransactionService transactionService;
    private final AssetService assetService;

    public TransactionController(
            TransactionService transactionService,
            AssetService assetService) {
        this.transactionService = transactionService;
        this.assetService = assetService;
    }

    // Transaction dashboard
    @GetMapping
    public String transactionPage(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/";
        }

        model.addAttribute("user", loggedUser);
        model.addAttribute(
                "totalBuy",
                transactionService.getTotalBuy(loggedUser));
        model.addAttribute(
                "totalSell",
                transactionService.getTotalSell(loggedUser));
        model.addAttribute(
                "recentTransactions",
                transactionService.getRecentTransactions(loggedUser));
        model.addAttribute(
                "successRate",
                transactionService.getSuccessRate(loggedUser));
        model.addAttribute(
                "canTrade",
                loggedUser.getRole() == Role.INVESTOR);
        model.addAttribute("dashboardUrl", dashboardUrl(loggedUser));

        return "Transaction";
    }

    // Buy selected asset
    @PostMapping("/buy")
    public String buyAsset(
            @ModelAttribute Transaction transaction,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/";
        }

        try {
            transactionService.buyAsset(transaction, loggedUser);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Asset purchased successfully.");
            return "redirect:/transactions";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage());
            return "redirect:/transactions/buy";
        }
    }

    // Sell selected asset
    @PostMapping("/sell")
    public String sellAsset(
            @ModelAttribute Transaction transaction,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/";
        }

        try {
            transactionService.sellAsset(transaction, loggedUser);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Asset sold successfully.");
            return "redirect:/transactions";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage());
            return "redirect:/transactions/sell";
        }
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Transaction> getAllTransactions(HttpSession session) {
        User loggedUser = getLoggedUser(session);
        return loggedUser == null
                ? List.of()
                : transactionService.getAllTransactions(loggedUser);
    }

    // Transaction history page
    @GetMapping({"/history", "/transaction-history"})
    public String transactionHistory(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/";
        }

        model.addAttribute("user", loggedUser);
        model.addAttribute(
                "transactions",
                transactionService.getAllTransactions(loggedUser));
        return "TransactionHistory";
    }

    @GetMapping({"/buy", "/buy-page"})
    public String buyPage(HttpSession session, Model model) {
        return prepareTradePage(session, model, "BuyAsset");
    }

    @GetMapping({"/sell", "/sell-page"})
    public String sellPage(HttpSession session, Model model) {
        return prepareTradePage(session, model, "SellAsset");
    }

    // Prepare buy/sell form data
    private String prepareTradePage(
            HttpSession session,
            Model model,
            String viewName) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/";
        }
        if (loggedUser.getRole() != Role.INVESTOR) {
            return "redirect:/transactions";
        }

        List<Asset> assets =
                assetService.getAssetsByInvestor(loggedUser.getUserId());
        model.addAttribute("user", loggedUser);
        model.addAttribute("assets", assets);
        model.addAttribute("transaction", new Transaction());
        return viewName;
    }

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
    }

    private String dashboardUrl(User user) {
        return switch (user.getRole()) {
            case ADMIN -> "/admin";
            case ADVISOR -> "/advisor";
            case INVESTOR -> "/investor";
        };
    }
}
