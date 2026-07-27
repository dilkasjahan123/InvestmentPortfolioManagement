package org.example.investmentfullproject.repository;

import org.example.investmentfullproject.model.PerformanceReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceReportRepository
        extends JpaRepository<PerformanceReport,Integer> {

    List<PerformanceReport>
    findByPortfolioPortfolioId(
            Integer portfolioId
    );

}