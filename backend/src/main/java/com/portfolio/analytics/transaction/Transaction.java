package com.portfolio.analytics.transaction;

import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.portfolio.Portfolio;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 16)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double price;

    @Column(name = "transaction_cost", nullable = false)
    private Double transactionCost = 0.0;

    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @Column(nullable = false, length = 8)
    private String currency = "INR";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Transaction() {
    }

    public Transaction(Long id, Portfolio portfolio, Asset asset, TransactionType transactionType,
                       Double quantity, Double price, Double transactionCost, Instant transactionDate,
                       String currency, Instant createdAt) {
        this.id = id;
        this.portfolio = portfolio;
        this.asset = asset;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.price = price;
        this.transactionCost = transactionCost != null ? transactionCost : 0.0;
        this.transactionDate = transactionDate != null ? transactionDate : Instant.now();
        this.currency = currency != null ? currency : "INR";
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.transactionDate == null) {
            this.transactionDate = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Portfolio getPortfolio() { return portfolio; }
    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getTransactionCost() { return transactionCost; }
    public void setTransactionCost(Double transactionCost) { this.transactionCost = transactionCost; }

    public Instant getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Instant transactionDate) { this.transactionDate = transactionDate; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Portfolio portfolio;
        private Asset asset;
        private TransactionType transactionType;
        private Double quantity;
        private Double price;
        private Double transactionCost = 0.0;
        private Instant transactionDate;
        private String currency = "INR";
        private Instant createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder portfolio(Portfolio portfolio) { this.portfolio = portfolio; return this; }
        public Builder asset(Asset asset) { this.asset = asset; return this; }
        public Builder transactionType(TransactionType transactionType) { this.transactionType = transactionType; return this; }
        public Builder quantity(Double quantity) { this.quantity = quantity; return this; }
        public Builder price(Double price) { this.price = price; return this; }
        public Builder transactionCost(Double transactionCost) { this.transactionCost = transactionCost; return this; }
        public Builder transactionDate(Instant transactionDate) { this.transactionDate = transactionDate; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public Transaction build() {
            return new Transaction(id, portfolio, asset, transactionType, quantity, price, transactionCost, transactionDate, currency, createdAt);
        }
    }
}
