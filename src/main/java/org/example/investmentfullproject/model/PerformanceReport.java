package org.example.investmentfullproject.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "performanceReport")
public class PerformanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reportId")
    private Integer reportId;

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    private Portfolio portfolio;

    private LocalDate reportDate;

    private BigDecimal returnPercentage;

    private BigDecimal riskScore;

    public PerformanceReport() {
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public BigDecimal getReturnPercentage() {
        return returnPercentage;
    }

    public void setReturnPercentage(
            BigDecimal returnPercentage) {
        this.returnPercentage = returnPercentage;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }
}