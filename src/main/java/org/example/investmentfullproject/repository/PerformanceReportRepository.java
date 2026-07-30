package org.example.investmentfullproject.repository;

import org.example.investmentfullproject.model.PerformanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReportRepository
        extends JpaRepository<PerformanceReport, Integer> {

    List<PerformanceReport>
    findByPortfolioPortfolioIdOrderByReportDateDescReportIdDesc(
            Integer portfolioId);
}
