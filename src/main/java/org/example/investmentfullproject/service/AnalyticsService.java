package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.PerformanceReport;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.RiskLevel;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PerformanceReportRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PerformanceReportRepository performanceReportRepository;

    public Double getPortfolioPerformance(
            Integer portfolioId) {

        List<Asset> assets =
                assetRepository.findByPortfolioPortfolioId(
                        portfolioId);

        double invested = 0;
        double current = 0;

        for (Asset asset : assets) {

            if(!asset.getActive()){
                continue;
            }

            invested +=
                    asset.getPurchasePrice()
                            .doubleValue()
                            * asset.getQuantity();

            current +=
                    asset.getCurrentPrice()
                            .doubleValue()
                            * asset.getQuantity();
        }

        if(invested == 0){
            return 0.0;
        }

        return ((current - invested)
                / invested) * 100;
    }
    public PerformanceReport generateReport(
            Integer portfolioId){

        Portfolio portfolio =
                portfolioRepository.findById(
                                portfolioId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Portfolio Not Found"
                                ));

        Double performance =
                getPortfolioPerformance(
                        portfolioId
                );

        PerformanceReport report =
                new PerformanceReport();

        report.setPortfolio(
                portfolio
        );

        report.setReportDate(
                LocalDate.now()
        );

        report.setReturnPercentage(
                BigDecimal.valueOf(
                        performance
                )
        );

        report.setRiskScore(
                BigDecimal.valueOf(
                        getRiskScore(
                                portfolioId
                        )
                )
        );

        return performanceReportRepository
                .save(report);
    }

    public Double getRiskScore(Integer portfolioId) {

        Portfolio portfolio =
                portfolioRepository.findById(portfolioId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Portfolio Not Found"));

        RiskLevel riskLevel =
                portfolio.getRiskLevel();

        switch (riskLevel) {

            case LOW:
                return 3.0;

            case MEDIUM:
                return 6.0;

            case HIGH:
                return 9.0;

            default:
                return 0.0;
        }
    }
    public List<PerformanceReport>
    getReportsByPortfolio(
            Integer portfolioId){

        return performanceReportRepository
                .findByPortfolioPortfolioId(
                        portfolioId
                );
    }
}