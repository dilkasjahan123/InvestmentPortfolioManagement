package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.model.RiskLevel;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.example.investmentfullproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void createPortfolioSuccess() {

        User investor = new User();
        investor.setUserId(1);
        investor.setRole(Role.INVESTOR);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName("Retirement");
        portfolio.setRiskLevel(RiskLevel.LOW);
        portfolio.setInvestor(investor);

        when(userRepository.findById(1))
                .thenReturn(Optional.of(investor));

        when(portfolioRepository.save(any(Portfolio.class)))
                .thenReturn(portfolio);

        Portfolio result =
                portfolioService.createPortfolio(portfolio);

        assertEquals("Retirement",
                result.getPortfolioName());
    }

    @Test
    void portfolioWithoutNameThrowsException() {

        Portfolio portfolio = new Portfolio();

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> portfolioService.createPortfolio(portfolio));

        assertEquals(
                "Portfolio name is required.",
                ex.getMessage());
    }

    @Test
    void portfolioWithoutInvestorThrowsException() {

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName("Growth");
        portfolio.setRiskLevel(RiskLevel.HIGH);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> portfolioService.createPortfolio(portfolio));

        assertEquals(
                "Please select an investor.",
                ex.getMessage());
    }
}