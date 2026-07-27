package org.example.investmentfullproject.repository;

import org.example.investmentfullproject.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository
        extends JpaRepository<Portfolio,Integer> {

    List<Portfolio> findByInvestorUserId(
            Integer userId);

}
