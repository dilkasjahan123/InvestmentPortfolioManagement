package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.example.investmentfullproject.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private UserRepository userRepository;

    public Portfolio createPortfolio(
            Portfolio portfolio) {

        Integer userId =
                portfolio.getInvestor().getUserId();

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User Not Found"
                                ));

        portfolio.setInvestor(user);

        return portfolioRepository.save(portfolio);
    }

    public Portfolio updatePortfolio(
            Portfolio portfolio) {

        return portfolioRepository.save(portfolio);
    }

    public Portfolio getPortfolioById(
            Integer portfolioId) {

        return portfolioRepository
                .findById(portfolioId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Portfolio Not Found"));
    }

    public List<Portfolio> getAllPortfolios() {

        return portfolioRepository.findAll();
    }

    @Autowired
    private AssetRepository assetRepository;

    public void deletePortfolio(Integer portfolioId) {

        List<Asset> assets =
                assetRepository
                        .findByPortfolioPortfolioId(
                                portfolioId
                        );

        if (!assets.isEmpty()) {

            throw new RuntimeException(
                    "Cannot delete portfolio. Remove associated assets first."
            );
        }

        portfolioRepository.deleteById(portfolioId);
    }

    public void updatePortfolioValue(Integer portfolioId){

        Portfolio portfolio =
                portfolioRepository.findById(portfolioId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Portfolio Not Found"
                                )
                        );

        List<Asset> assets =
                assetRepository
                        .findByPortfolioPortfolioId(
                                portfolioId
                        );

        BigDecimal totalValue =
                assets.stream()
                        .filter(asset ->
                                Boolean.TRUE.equals(
                                        asset.getActive()
                                )
                        )
                        .map(asset ->
                                asset.getCurrentPrice()
                                        .multiply(
                                                BigDecimal.valueOf(
                                                        asset.getQuantity()
                                                )
                                        )
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        portfolio.setTotalValue(
                totalValue
        );

        portfolioRepository.save(
                portfolio
        );
    }
    public void recalculateAllPortfolioValues() {

        List<Portfolio> portfolios =
                portfolioRepository.findAll();

        for(Portfolio portfolio : portfolios){

            updatePortfolioValue(
                    portfolio.getPortfolioId()
            );
        }
    }
}
