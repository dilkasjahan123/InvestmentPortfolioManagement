package org.example.investmentfullproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@Entity
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assetId")
    private Integer assetId;

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    private Portfolio portfolio;

    @Column(name = "assetName")
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "assetType")
    private AssetType assetType;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "purchasePrice")
    private BigDecimal purchasePrice;

    @Column(name = "currentPrice")
    private BigDecimal currentPrice;

    @Column(name = "active")
    private Boolean active = true;

    public Asset() {
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Calculate total invested amount
    @Transient
    public BigDecimal getInvestedValue() {
        if (purchasePrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }

        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Calculate current market value
    @Transient
    public BigDecimal getCurrentValue() {
        if (currentPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }

        return currentPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Calculate unrealized profit or loss
    @Transient
    public BigDecimal getProfitLoss() {
        return getCurrentValue().subtract(getInvestedValue());
    }

    // Determine whether the asset is profitable
    @Transient
    public boolean isProfitable() {
        return getProfitLoss().compareTo(BigDecimal.ZERO) >= 0;
    }
}
