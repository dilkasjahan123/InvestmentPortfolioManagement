package org.example.investmentfullproject.controller;

import org.example.investmentfullproject.model.PerformanceReport;
import org.example.investmentfullproject.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/portfolio/{portfolioId}")
    public Double getPortfolioPerformance(
            @PathVariable Integer portfolioId) {

        return analyticsService
                .getPortfolioPerformance(portfolioId);
    }

    @GetMapping("/risk/{portfolioId}")
    public Double getRiskScore(
            @PathVariable Integer portfolioId) {

        return analyticsService
                .getRiskScore(portfolioId);
    }
    @PostMapping("/report/{portfolioId}")
    public PerformanceReport
    generateReport(
            @PathVariable Integer portfolioId){

        return analyticsService
                .generateReport(
                        portfolioId
                );
    }
    @GetMapping("/reports/{portfolioId}")
    public List<PerformanceReport>
    getReports(
            @PathVariable Integer portfolioId){

        return analyticsService
                .getReportsByPortfolio(
                        portfolioId
                );
    }
}