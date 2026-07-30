package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.Portfolio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record PortfolioAnalytics(
        Portfolio portfolio,
        List<Asset> assets,
        BigDecimal investedValue,
        BigDecimal currentValue,
        BigDecimal profitLoss,
        BigDecimal returnPercentage,
        BigDecimal riskScore,
        Map<String, BigDecimal> allocationByType) {
}
