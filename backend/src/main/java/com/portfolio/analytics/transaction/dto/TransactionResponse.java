package com.portfolio.analytics.transaction.dto;

import com.portfolio.analytics.transaction.Transaction;
import com.portfolio.analytics.transaction.TransactionType;
import java.time.Instant;

public class TransactionResponse {

    private Long id;
    private Long portfolioId;
    private Long assetId;
    private String symbol;
    private String assetName;
    private TransactionType transactionType;
    private Double quantity;
    private Double price;
    private Double totalAmount;
    private Double transactionCost;
    private String currency;
    private Instant transactionDate;
    private Instant createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, Long portfolioId, Long assetId, String symbol,
                               String assetName, TransactionType transactionType,
                               Double quantity, Double price, Double totalAmount,
                               Double transactionCost, String currency,
                               Instant transactionDate, Instant createdAt) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.assetId = assetId;
        this.symbol = symbol;
        this.assetName = assetName;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.transactionCost = transactionCost;
        this.currency = currency;
        this.transactionDate = transactionDate;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TransactionResponse fromEntity(Transaction entity) {
        double total = (entity.getQuantity() * entity.getPrice()) + (entity.getTransactionCost() != null ? entity.getTransactionCost() : 0.0);
        return builder()
                .id(entity.getId())
                .portfolioId(entity.getPortfolio().getId())
                .assetId(entity.getAsset().getId())
                .symbol(entity.getAsset().getSymbol())
                .assetName(entity.getAsset().getName())
                .transactionType(entity.getTransactionType())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .totalAmount(Math.round(total * 100.0) / 100.0)
                .transactionCost(entity.getTransactionCost())
                .currency(entity.getCurrency())
                .transactionDate(entity.getTransactionDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(Double transactionCost) {
        this.transactionCost = transactionCost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private Long id;
        private Long portfolioId;
        private Long assetId;
        private String symbol;
        private String assetName;
        private TransactionType transactionType;
        private Double quantity;
        private Double price;
        private Double totalAmount;
        private Double transactionCost;
        private String currency;
        private Instant transactionDate;
        private Instant createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder portfolioId(Long portfolioId) {
            this.portfolioId = portfolioId;
            return this;
        }

        public Builder assetId(Long assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder assetName(String assetName) {
            this.assetName = assetName;
            return this;
        }

        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder totalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder transactionCost(Double transactionCost) {
            this.transactionCost = transactionCost;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder transactionDate(Instant transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TransactionResponse build() {
            return new TransactionResponse(id, portfolioId, assetId, symbol, assetName,
                    transactionType, quantity, price, totalAmount, transactionCost,
                    currency, transactionDate, createdAt);
        }
    }
}
