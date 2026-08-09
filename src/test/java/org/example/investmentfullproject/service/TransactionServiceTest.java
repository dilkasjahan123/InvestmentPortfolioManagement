package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.*;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.TransactionRepository;
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
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void buyAssetSuccess() {

        User investor = new User();
        investor.setUserId(1);
        investor.setRole(Role.INVESTOR);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(1);
        portfolio.setInvestor(investor);

        Asset asset = new Asset();
        asset.setAssetId(1);
        asset.setPortfolio(portfolio);
        asset.setCurrentPrice(new BigDecimal("100"));
        asset.setQuantity(10);
        asset.setPurchasePrice(new BigDecimal("100"));
        asset.setActive(true);

        Transaction transaction = new Transaction();
        transaction.setAssetId(1);
        transaction.setQuantity(5);

        when(assetRepository.findById(1))
                .thenReturn(Optional.of(asset));

        transactionService.buyAsset(
                transaction,
                investor);

        assertEquals(
                15,
                asset.getQuantity());
    }

    @Test
    void sellMoreThanAvailableThrowsException() {

        User investor = new User();
        investor.setUserId(1);
        investor.setRole(Role.INVESTOR);

        Portfolio portfolio = new Portfolio();
        portfolio.setInvestor(investor);

        Asset asset = new Asset();
        asset.setAssetId(1);
        asset.setPortfolio(portfolio);
        asset.setCurrentPrice(new BigDecimal("100"));
        asset.setQuantity(10);
        asset.setActive(true);

        Transaction transaction = new Transaction();
        transaction.setAssetId(1);
        transaction.setQuantity(20);

        when(assetRepository.findById(1))
                .thenReturn(Optional.of(asset));

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.sellAsset(
                                transaction,
                                investor));

        assertEquals(
                "Sell quantity cannot exceed the available quantity.",
                ex.getMessage());
    }

    @Test
    void sellAssetSuccess() {

        User investor = new User();
        investor.setUserId(1);
        investor.setRole(Role.INVESTOR);

        Portfolio portfolio = new Portfolio();
        portfolio.setInvestor(investor);

        Asset asset = new Asset();
        asset.setAssetId(1);
        asset.setPortfolio(portfolio);
        asset.setCurrentPrice(new BigDecimal("100"));
        asset.setQuantity(10);
        asset.setActive(true);

        Transaction transaction = new Transaction();
        transaction.setAssetId(1);
        transaction.setQuantity(4);

        when(assetRepository.findById(1))
                .thenReturn(Optional.of(asset));

        transactionService.sellAsset(
                transaction,
                investor);

        assertEquals(
                6,
                asset.getQuantity());
    }
}