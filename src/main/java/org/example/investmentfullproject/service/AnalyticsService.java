package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.PerformanceReport;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.RiskLevel;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PerformanceReportRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private static final int PERCENT_SCALE = 2;

    private final AssetRepository assetRepository;
    private final PortfolioRepository portfolioRepository;
    private final PerformanceReportRepository performanceReportRepository;

    public AnalyticsService(
            AssetRepository assetRepository,
            PortfolioRepository portfolioRepository,
            PerformanceReportRepository performanceReportRepository) {
        this.assetRepository = assetRepository;
        this.portfolioRepository = portfolioRepository;
        this.performanceReportRepository = performanceReportRepository;
    }

    // Calculate portfolio return percentage
    public Double getPortfolioPerformance(Integer portfolioId) {
        return getAnalytics(portfolioId)
                .returnPercentage()
                .doubleValue();
    }

    // Calculate portfolio risk score
    public Double getRiskScore(Integer portfolioId) {
        return calculateRiskScore(requirePortfolio(portfolioId))
                .doubleValue();
    }

    // Build portfolio analytics summary
    public PortfolioAnalytics getAnalytics(Integer portfolioId) {
        Portfolio portfolio = requirePortfolio(portfolioId);
        List<Asset> assets =
                assetRepository.findByPortfolioPortfolioIdAndActiveTrue(
                        portfolioId);

        BigDecimal investedValue = assets.stream()
                .map(Asset::getInvestedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentValue = assets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profitLoss = currentValue.subtract(investedValue);
        BigDecimal returnPercentage =
                percentage(profitLoss, investedValue);

        Map<String, BigDecimal> allocationByType =
                calculateAllocation(assets, currentValue);

        return new PortfolioAnalytics(
                portfolio,
                List.copyOf(assets),
                investedValue,
                currentValue,
                profitLoss,
                returnPercentage,
                calculateRiskScore(portfolio),
                allocationByType);
    }

    // Create and save performance report snapshot
    public PerformanceReport generateReport(Integer portfolioId) {
        PortfolioAnalytics analytics = getAnalytics(portfolioId);

        PerformanceReport report = new PerformanceReport();
        report.setPortfolio(analytics.portfolio());
        report.setReportDate(LocalDate.now());
        report.setReturnPercentage(analytics.returnPercentage());
        report.setRiskScore(analytics.riskScore());

        return performanceReportRepository.save(report);
    }

    // Retrieve report history for a portfolio
    public List<PerformanceReport> getReportsByPortfolio(
            Integer portfolioId) {
        requirePortfolio(portfolioId);
        return performanceReportRepository
                .findByPortfolioPortfolioIdOrderByReportDateDescReportIdDesc(
                        portfolioId);
    }

    // Validate and retrieve portfolio
    private Portfolio requirePortfolio(Integer portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new RuntimeException("Portfolio Not Found"));
    }

    // Convert risk level to numeric score
    private BigDecimal calculateRiskScore(Portfolio portfolio) {
        RiskLevel riskLevel = portfolio.getRiskLevel();

        if (riskLevel == null) {
            return BigDecimal.ZERO;
        }

        return switch (riskLevel) {
            case LOW -> BigDecimal.valueOf(3);
            case MEDIUM -> BigDecimal.valueOf(6);
            case HIGH -> BigDecimal.valueOf(9);
        };
    }

    // Calculate allocation percentage by asset type
    private Map<String, BigDecimal> calculateAllocation(
            List<Asset> assets,
            BigDecimal totalCurrentValue) {

        Map<String, BigDecimal> valuesByType = new LinkedHashMap<>();

        for (Asset asset : assets) {
            String type = asset.getAssetType() == null
                    ? "OTHER"
                    : asset.getAssetType().name();

            valuesByType.merge(
                    type,
                    asset.getCurrentValue(),
                    BigDecimal::add);
        }

        Map<String, BigDecimal> allocation = new LinkedHashMap<>();
        valuesByType.forEach((type, value) ->
                allocation.put(
                        type,
                        percentage(value, totalCurrentValue)));

        return allocation;
    }

    // Calculate percentage value
    private BigDecimal percentage(
            BigDecimal value,
            BigDecimal total) {

        if (total == null
                || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(PERCENT_SCALE);
        }

        return value
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        total,
                        PERCENT_SCALE,
                        RoundingMode.HALF_UP);
    }
}
