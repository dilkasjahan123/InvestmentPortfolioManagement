package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AssetService {

    private final PortfolioService portfolioService;
    private final AssetRepository assetRepository;
    private final PortfolioRepository portfolioRepository;

    public AssetService(
            PortfolioService portfolioService,
            AssetRepository assetRepository,
            PortfolioRepository portfolioRepository) {
        this.portfolioService = portfolioService;
        this.assetRepository = assetRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public Asset addAsset(Asset asset) {
        validateAsset(asset);

        Portfolio portfolio = findPortfolio(
                asset.getPortfolio().getPortfolioId());

        asset.setAssetId(null);
        asset.setPortfolio(portfolio);
        asset.setAssetName(asset.getAssetName().trim());
        asset.setActive(true);

        Asset savedAsset = assetRepository.save(asset);
        portfolioService.updatePortfolioValue(portfolio.getPortfolioId());

        return savedAsset;
    }

    public Asset updateAsset(Asset submittedAsset) {
        validateAsset(submittedAsset);

        if (submittedAsset.getAssetId() == null) {
            throw new RuntimeException("Asset ID is required.");
        }

        Asset existingAsset = getActiveAssetById(submittedAsset.getAssetId());
        Integer oldPortfolioId =
                existingAsset.getPortfolio().getPortfolioId();

        Portfolio selectedPortfolio = findPortfolio(
                submittedAsset.getPortfolio().getPortfolioId());

        existingAsset.setPortfolio(selectedPortfolio);
        existingAsset.setAssetName(submittedAsset.getAssetName().trim());
        existingAsset.setAssetType(submittedAsset.getAssetType());
        existingAsset.setQuantity(submittedAsset.getQuantity());
        existingAsset.setPurchasePrice(submittedAsset.getPurchasePrice());
        existingAsset.setCurrentPrice(submittedAsset.getCurrentPrice());

        Asset updatedAsset = assetRepository.save(existingAsset);
        Integer newPortfolioId = selectedPortfolio.getPortfolioId();

        portfolioService.updatePortfolioValue(oldPortfolioId);

        if (!oldPortfolioId.equals(newPortfolioId)) {
            portfolioService.updatePortfolioValue(newPortfolioId);
        }

        return updatedAsset;
    }

    public Asset getActiveAssetById(Integer assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found."));

        if (!Boolean.TRUE.equals(asset.getActive())) {
            throw new RuntimeException("Asset is inactive.");
        }

        return asset;
    }

    public List<Asset> getAssetsByPortfolio(Integer portfolioId) {
        return sortAssets(
                assetRepository.findByPortfolioPortfolioIdAndActiveTrue(portfolioId));
    }

    public List<Asset> getAllAssets() {
        return sortAssets(assetRepository.findByActiveTrue());
    }

    public List<Asset> getAssetsByInvestor(Integer userId) {
        return sortAssets(
                assetRepository.findByActiveTrueAndPortfolioInvestorUserId(userId));
    }

    public void deleteAsset(Integer assetId) {
        Asset asset = getActiveAssetById(assetId);
        Integer portfolioId = asset.getPortfolio().getPortfolioId();

        asset.setActive(false);
        assetRepository.save(asset);

        portfolioService.updatePortfolioValue(portfolioId);
    }

    public long getAssetCount() {
        return assetRepository.countByActiveTrue();
    }

    private Portfolio findPortfolio(Integer portfolioId) {
        if (portfolioId == null) {
            throw new RuntimeException("Please select a portfolio.");
        }

        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found."));
    }

    private void validateAsset(Asset asset) {
        if (asset.getAssetName() == null || asset.getAssetName().isBlank()) {
            throw new RuntimeException("Asset name is required.");
        }

        if (asset.getPortfolio() == null
                || asset.getPortfolio().getPortfolioId() == null) {
            throw new RuntimeException("Please select a portfolio.");
        }

        if (asset.getAssetType() == null) {
            throw new RuntimeException("Please select an asset type.");
        }

        if (asset.getQuantity() == null || asset.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than zero.");
        }

        if (asset.getPurchasePrice() == null
                || asset.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Purchase price must be greater than zero.");
        }

        if (asset.getCurrentPrice() == null
                || asset.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Current price must be greater than zero.");
        }
    }

    private List<Asset> sortAssets(List<Asset> assets) {
        List<Asset> sortedAssets = new ArrayList<>(assets);

        sortedAssets.sort(
                Comparator.comparing(
                                (Asset asset) ->
                                        asset.getPortfolio() == null
                                                || asset.getPortfolio().getInvestor() == null
                                                || asset.getPortfolio()
                                                .getInvestor().getUsername() == null
                                                ? ""
                                                : asset.getPortfolio()
                                                .getInvestor().getUsername(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(
                                asset -> asset.getPortfolio() == null
                                        || asset.getPortfolio()
                                        .getPortfolioName() == null
                                        ? ""
                                        : asset.getPortfolio().getPortfolioName(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(
                                Asset::getAssetName,
                                Comparator.nullsLast(
                                        String.CASE_INSENSITIVE_ORDER)));

        return sortedAssets;
    }
}
