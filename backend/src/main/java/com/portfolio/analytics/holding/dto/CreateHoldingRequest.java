package com.portfolio.analytics.holding.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public class CreateHoldingRequest {

    private Long assetId;
    private String symbol;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Double quantity;

    @NotNull(message = "Average cost is required")
    @Positive(message = "Average cost must be greater than zero")
    private Double averageCost;

    @DecimalMin(value = "0.0", message = "Target weight must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Target weight must not exceed 1.0")
    private Double targetWeight;

    private Instant purchaseDate;

    public CreateHoldingRequest() {
    }

    public CreateHoldingRequest(Long assetId, String symbol, Double quantity, Double averageCost,
                                Double targetWeight, Instant purchaseDate) {
        this.assetId = assetId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.targetWeight = targetWeight;
        this.purchaseDate = purchaseDate;
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Double averageCost) {
        this.averageCost = averageCost;
    }

    public Double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public static class Builder {
        private Long assetId;
        private String symbol;
        private Double quantity;
        private Double averageCost;
        private Double targetWeight;
        private Instant purchaseDate;

        public Builder assetId(Long assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder averageCost(Double averageCost) {
            this.averageCost = averageCost;
            return this;
        }

        public Builder targetWeight(Double targetWeight) {
            this.targetWeight = targetWeight;
            return this;
        }

        public Builder purchaseDate(Instant purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public CreateHoldingRequest build() {
            return new CreateHoldingRequest(assetId, symbol, quantity, averageCost, targetWeight, purchaseDate);
        }
    }
}
