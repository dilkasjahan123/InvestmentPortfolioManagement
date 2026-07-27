package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {
    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    public Asset addAsset(Asset asset) {

        Integer portfolioId =
                asset.getPortfolio().getPortfolioId();

        Portfolio portfolio =
                portfolioRepository.findById(portfolioId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Portfolio not found"
                                ));

        asset.setPortfolio(portfolio);

        Asset savedAsset =
                assetRepository.save(asset);

        portfolioService.updatePortfolioValue(
                portfolioId
        );

        return savedAsset;

    }

    public Asset updateAsset(Asset asset) {

        Asset existingAsset =
                assetRepository.findById(
                                asset.getAssetId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Asset not found"
                                ));

        existingAsset.setAssetName(
                asset.getAssetName()
        );

        existingAsset.setAssetType(
                asset.getAssetType()
        );


        existingAsset.setPurchasePrice(
                asset.getPurchasePrice()
        );

        existingAsset.setCurrentPrice(
                asset.getCurrentPrice()
        );

        Asset updatedAsset =
                assetRepository.save(
                        existingAsset
                );

        portfolioService.updatePortfolioValue(
                updatedAsset.getPortfolio()
                        .getPortfolioId()
        );

        return updatedAsset;
    }

    public List<Asset> getAssetsByPortfolio(
            Integer portfolioId) {

        return assetRepository
                .findByPortfolioPortfolioId(
                        portfolioId
                );
    }

    public void deleteAsset(
            Integer assetId) {

        Asset asset =
                assetRepository.findById(assetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Asset not found"
                                ));

        Integer portfolioId =
                asset.getPortfolio()
                        .getPortfolioId();

        asset.setActive(false);

        assetRepository.save(asset);

        portfolioService.updatePortfolioValue(
                portfolioId
        );
    }
    public List<Asset> getAllAssets(){

        return assetRepository.findByActiveTrue();
    }
    public long getAssetCount(){

        return assetRepository
                .findByActiveTrue()
                .size();
    }
}