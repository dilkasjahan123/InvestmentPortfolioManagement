package org.example.investmentfullproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "Transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionId")
    private Integer transactionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assetId")
    private Asset asset;

    @Column(name = "transactionType")
    private String transactionType;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "transactionDate")
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "price")
    private BigDecimal price;

    public enum TransactionStatus {
        SUCCESS,
        FAILED
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    @JsonIgnore
    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Transient
    public Integer getAssetId() {
        return asset == null ? null : asset.getAssetId();
    }

    public void setAssetId(Integer assetId) {
        if (assetId == null) {
            asset = null;
            return;
        }

        Asset selectedAsset = new Asset();
        selectedAsset.setAssetId(assetId);
        asset = selectedAsset;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Transient
    public BigDecimal getTotalAmount() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Transient
    public LocalTime getTransactionTime() {
        return transactionDate == null ? null : transactionDate.toLocalTime();
    }
}
