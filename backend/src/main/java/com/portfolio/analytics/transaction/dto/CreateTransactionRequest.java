package com.portfolio.analytics.transaction.dto;

import com.portfolio.analytics.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public class CreateTransactionRequest {

    private Long assetId;
    private String symbol;

    @NotNull(message = "Transaction type is required (BUY, SELL, DIVIDEND, FEE)")
    private TransactionType transactionType;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    private Double transactionCost = 0.0;

    private Instant transactionDate;
    private String currency;

    public CreateTransactionRequest() {
    }

    public CreateTransactionRequest(Long assetId, String symbol, TransactionType transactionType,
                                    Double quantity, Double price, Double transactionCost,
                                    Instant transactionDate, String currency) {
        this.assetId = assetId;
        this.symbol = symbol;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.price = price;
        this.transactionCost = transactionCost != null ? transactionCost : 0.0;
        this.transactionDate = transactionDate;
        this.currency = currency;
    }

    public static Builder builder() {
        return new Builder();
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

    public Double getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(Double transactionCost) {
        this.transactionCost = transactionCost;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static class Builder {
        private Long assetId;
        private String symbol;
        private TransactionType transactionType;
        private Double quantity;
        private Double price;
        private Double transactionCost = 0.0;
        private Instant transactionDate;
        private String currency;

        public Builder assetId(Long assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder symbol(String symbol) {
            this.symbol = symbol;
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

        public Builder transactionCost(Double transactionCost) {
            this.transactionCost = transactionCost;
            return this;
        }

        public Builder transactionDate(Instant transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public CreateTransactionRequest build() {
            return new CreateTransactionRequest(assetId, symbol, transactionType, quantity, price, transactionCost, transactionDate, currency);
        }
    }
}
