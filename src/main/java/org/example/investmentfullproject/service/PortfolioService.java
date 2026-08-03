package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.example.investmentfullproject.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            UserRepository userRepository,
            AssetRepository assetRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
    }

    public Portfolio createPortfolio(Portfolio portfolio) {
        validatePortfolio(portfolio);

        User investor = findInvestor(portfolio.getInvestor().getUserId());
        portfolio.setInvestor(investor);
        portfolio.setPortfolioId(null);

        portfolio.setPortfolioName(portfolio.getPortfolioName().trim());
        portfolio.setTotalValue(BigDecimal.ZERO);

        return portfolioRepository.save(portfolio);
    }

    public Portfolio updatePortfolio(Portfolio submittedPortfolio) {
        validatePortfolio(submittedPortfolio);

        Portfolio existingPortfolio = getPortfolioById(
                submittedPortfolio.getPortfolioId());

        existingPortfolio.setPortfolioName(
                submittedPortfolio.getPortfolioName().trim());
        existingPortfolio.setRiskLevel(submittedPortfolio.getRiskLevel());

        if (submittedPortfolio.getInvestor() != null
                && submittedPortfolio.getInvestor().getUserId() != null) {
            existingPortfolio.setInvestor(
                    findInvestor(submittedPortfolio.getInvestor().getUserId()));
        }

        // totalValue is calculated from assets, not accepted from the form.
        return portfolioRepository.save(existingPortfolio);
    }

    public Portfolio getPortfolioById(Integer portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio Not Found"));
    }

    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public List<Portfolio> getPortfoliosByInvestor(Integer userId) {
        return portfolioRepository.findByInvestorUserId(userId);
    }

    public void deletePortfolio(Integer portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        List<Asset> assets =
                assetRepository.findByPortfolioPortfolioId(portfolioId);

        if (!assets.isEmpty()) {
            throw new RuntimeException(
                    "Cannot delete portfolio because it contains asset history.");
        }

        portfolioRepository.delete(portfolio);
    }

    public void updatePortfolioValue(Integer portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        List<Asset> assets =
                assetRepository.findByPortfolioPortfolioId(portfolioId);

        BigDecimal totalValue = assets.stream()
                .filter(asset -> Boolean.TRUE.equals(asset.getActive()))
                .map(asset -> asset.getCurrentPrice()
                        .multiply(BigDecimal.valueOf(asset.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        portfolio.setTotalValue(totalValue);
        portfolioRepository.save(portfolio);
    }

    public void recalculateAllPortfolioValues() {
        portfolioRepository.findAll()
                .forEach(portfolio ->
                        updatePortfolioValue(portfolio.getPortfolioId()));
    }

    private User findInvestor(Integer userId) {
        if (userId == null) {
            throw new RuntimeException("Please select an investor.");
        }

        User investor = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Investor Not Found"));

        if (investor.getRole() != Role.INVESTOR) {
            throw new RuntimeException(
                    "Selected user is not an investor.");
        }

        return investor;
    }

    private void validatePortfolio(Portfolio portfolio) {
        if (portfolio.getPortfolioName() == null
                || portfolio.getPortfolioName().isBlank()) {
            throw new RuntimeException("Portfolio name is required.");
        }

        if (portfolio.getRiskLevel() == null) {
            throw new RuntimeException("Risk level is required.");
        }

        if (portfolio.getInvestor() == null) {
            throw new RuntimeException("Please select an investor.");
        }
    }
}
