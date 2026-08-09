package org.example.investmentfullproject.controller;

import org.example.investmentfullproject.model.PerformanceReport;
import org.example.investmentfullproject.service.AnalyticsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Portfolio performance percentage
    @GetMapping("/portfolio/{portfolioId}")
    public Double getPortfolioPerformance(
            @PathVariable Integer portfolioId) {
        return analyticsService.getPortfolioPerformance(portfolioId);
    }

    // Portfolio risk score
    @GetMapping("/risk/{portfolioId}")
    public Double getRiskScore(
            @PathVariable Integer portfolioId) {
        return analyticsService.getRiskScore(portfolioId);
    }

    // Generate performance report snapshot
    @PostMapping("/report/{portfolioId}")
    public PerformanceReport generateReport(
            @PathVariable Integer portfolioId) {
        return analyticsService.generateReport(portfolioId);
    }

    // Retrieve generated reports for a portfolio
    @GetMapping("/reports/{portfolioId}")
    public List<PerformanceReport> getReports(
            @PathVariable Integer portfolioId) {
        return analyticsService.getReportsByPortfolio(portfolioId);
    }
}
