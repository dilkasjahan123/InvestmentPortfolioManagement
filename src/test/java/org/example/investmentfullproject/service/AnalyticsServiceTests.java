package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.AssetType;
import org.example.investmentfullproject.model.PerformanceReport;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.RiskLevel;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PerformanceReportRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnalyticsServiceTests {

    private AssetRepository assetRepository;
    private PortfolioRepository portfolioRepository;
    private PerformanceReportRepository reportRepository;
    private AnalyticsService analyticsService;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        assetRepository = mock(AssetRepository.class);
        portfolioRepository = mock(PortfolioRepository.class);
        reportRepository = mock(PerformanceReportRepository.class);
        analyticsService = new AnalyticsService(
                assetRepository,
                portfolioRepository,
                reportRepository);

        portfolio = new Portfolio();
        portfolio.setPortfolioId(7);
        portfolio.setPortfolioName("Growth");
        portfolio.setRiskLevel(RiskLevel.MEDIUM);

        when(portfolioRepository.findById(7))
                .thenReturn(Optional.of(portfolio));
    }

    @Test
    void calculatesWeightedReturnAndAllocationFromActiveAssets() {
        Asset stock = asset(
                AssetType.Stock,
                2,
                "100.00",
                "120.00");
        Asset bond = asset(
                AssetType.Bond,
                1,
                "200.00",
                "180.00");

        when(assetRepository
                .findByPortfolioPortfolioIdAndActiveTrue(7))
                .thenReturn(List.of(stock, bond));

        PortfolioAnalytics result =
                analyticsService.getAnalytics(7);

        assertThat(result.investedValue())
                .isEqualByComparingTo("400.00");
        assertThat(result.currentValue())
                .isEqualByComparingTo("420.00");
        assertThat(result.profitLoss())
                .isEqualByComparingTo("20.00");
        assertThat(result.returnPercentage())
                .isEqualByComparingTo("5.00");
        assertThat(result.riskScore())
                .isEqualByComparingTo("6");
        assertThat(result.allocationByType().get("Stock"))
                .isEqualByComparingTo("57.14");
        assertThat(result.allocationByType().get("Bond"))
                .isEqualByComparingTo("42.86");
    }

    @Test
    void generatesPersistedSnapshotFromCurrentAnalytics() {
        when(assetRepository
                .findByPortfolioPortfolioIdAndActiveTrue(7))
                .thenReturn(List.of(
                        asset(
                                AssetType.Mutual_Fund,
                                5,
                                "10.00",
                                "12.00")));

        when(reportRepository.save(any(PerformanceReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PerformanceReport report =
                analyticsService.generateReport(7);

        assertThat(report.getPortfolio()).isSameAs(portfolio);
        assertThat(report.getReturnPercentage())
                .isEqualByComparingTo("20.00");
        assertThat(report.getRiskScore())
                .isEqualByComparingTo("6");
        assertThat(report.getReportDate()).isNotNull();
        verify(reportRepository).save(report);
    }

    private Asset asset(
            AssetType type,
            int quantity,
            String purchasePrice,
            String currentPrice) {

        Asset asset = new Asset();
        asset.setPortfolio(portfolio);
        asset.setAssetType(type);
        asset.setQuantity(quantity);
        asset.setPurchasePrice(new BigDecimal(purchasePrice));
        asset.setCurrentPrice(new BigDecimal(currentPrice));
        asset.setActive(true);
        return asset;
    }
}
