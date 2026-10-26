package com.portfolio.analytics.holding.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

public class UpdateHoldingRequest {

    @Positive(message = "Quantity must be greater than zero")
    private Double quantity;

    @Positive(message = "Average cost must be greater than zero")
    private Double averageCost;

    @DecimalMin(value = "0.0", message = "Target weight must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Target weight must not exceed 1.0")
    private Double targetWeight;

    public UpdateHoldingRequest() {
    }

    public UpdateHoldingRequest(Double quantity, Double averageCost, Double targetWeight) {
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.targetWeight = targetWeight;
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private Double quantity;
        private Double averageCost;
        private Double targetWeight;

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

        public UpdateHoldingRequest build() {
            return new UpdateHoldingRequest(quantity, averageCost, targetWeight);
        }
    }
}
