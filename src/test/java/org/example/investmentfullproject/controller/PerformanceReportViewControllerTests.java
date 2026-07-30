package org.example.investmentfullproject.controller;

import org.example.investmentfullproject.model.PerformanceReport;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.RiskLevel;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.service.AnalyticsService;
import org.example.investmentfullproject.service.PortfolioAnalytics;
import org.example.investmentfullproject.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PerformanceReportViewController.class)
class PerformanceReportViewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private PortfolioService portfolioService;

    @Test
    void rendersAnalyticsDashboardForLoggedInInvestor() throws Exception {
        User investor = new User();
        investor.setUserId(11);
        investor.setUsername("Asha");
        investor.setRole(Role.INVESTOR);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(7);
        portfolio.setPortfolioName("Growth");
        portfolio.setRiskLevel(RiskLevel.MEDIUM);
        portfolio.setInvestor(investor);

        LinkedHashMap<String, BigDecimal> allocation =
                new LinkedHashMap<>();
        allocation.put("Stock", new BigDecimal("100.00"));

        PortfolioAnalytics analytics = new PortfolioAnalytics(
                portfolio,
                List.of(),
                new BigDecimal("1000.00"),
                new BigDecimal("1120.00"),
                new BigDecimal("120.00"),
                new BigDecimal("12.00"),
                new BigDecimal("6"),
                allocation);

        PerformanceReport report = new PerformanceReport();
        report.setReportId(3);
        report.setPortfolio(portfolio);
        report.setReportDate(LocalDate.of(2026, 7, 30));
        report.setReturnPercentage(new BigDecimal("12.00"));
        report.setRiskScore(new BigDecimal("6"));

        when(portfolioService.getPortfoliosByInvestor(11))
                .thenReturn(List.of(portfolio));
        when(analyticsService.getAnalytics(7))
                .thenReturn(analytics);
        when(analyticsService.getReportsByPortfolio(7))
                .thenReturn(List.of(report));

        mockMvc.perform(get("/performance-report")
                        .sessionAttr("loggedUser", investor))
                .andExpect(status().isOk())
                .andExpect(view().name("performance-report"))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Performance Analytics")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "30 July 2026")));
    }
}
