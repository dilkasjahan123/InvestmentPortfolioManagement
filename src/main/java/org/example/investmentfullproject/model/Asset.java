package org.example.investmentfullproject.model;

import jakarta.persistence.*;

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

    public Asset(Integer assetId,
                 Portfolio portfolio,
                 String assetName,
                 AssetType assetType,
                 Integer quantity,
                 BigDecimal purchasePrice,
                 BigDecimal currentPrice) {

        this.assetId = assetId;
        this.portfolio = portfolio;
        this.assetName = assetName;
        this.assetType = assetType;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.currentPrice = currentPrice;
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
}