package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.AssetType;
import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioService portfolioService;


    @InjectMocks
    private AssetService assetService;



    @Test
    void addAssetSuccess() {

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(1);

        Asset asset = new Asset();
        asset.setAssetName("Apple");
        asset.setAssetType(AssetType.Stock);
        asset.setCurrentPrice(new BigDecimal("100"));
        asset.setPortfolio(portfolio);

        when(portfolioRepository.findById(1))
                .thenReturn(Optional.of(portfolio));

        when(assetRepository.save(any(Asset.class)))
                .thenReturn(asset);

        Asset result = assetService.addAsset(asset);

        assertEquals("Apple", result.getAssetName());
    }

    @Test
    void invalidPriceThrowsException() {

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(1);

        Asset asset = new Asset();
        asset.setAssetName("Apple");
        asset.setAssetType(AssetType.Stock);
        asset.setPortfolio(portfolio);
        asset.setCurrentPrice(BigDecimal.ZERO);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> assetService.addAsset(asset));

        assertEquals(
                "Current price must be greater than zero.",
                ex.getMessage());
    }

    @Test
    void getAssetByIdNotFound() {

        when(assetRepository.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> assetService.getActiveAssetById(1));

        assertEquals(
                "Asset not found.",
                ex.getMessage());
    }
}