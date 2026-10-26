package com.portfolio.analytics.optimization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class OptimizationApiRequest {

    @Schema(description = "Optimization methodology: MAX_SHARPE, MIN_VARIANCE, or EQUAL_WEIGHT", example = "MAX_SHARPE")
    private String method = "MAX_SHARPE";

    @Schema(description = "Historical lookback period: 1Y, 3Y, 5Y, MAX", example = "3Y")
    private String lookback = "3Y";

    @DecimalMin(value = "0.01", message = "Max weight must be at least 0.01")
    @DecimalMax(value = "1.0", message = "Max weight cannot exceed 1.0")
    @Schema(description = "Maximum individual asset weight constraint", example = "0.30")
    private Double maxWeight = 1.0;

    public OptimizationApiRequest() {
    }

    public OptimizationApiRequest(String method, String lookback, Double maxWeight) {
        this.method = method != null ? method : "MAX_SHARPE";
        this.lookback = lookback != null ? lookback : "3Y";
        this.maxWeight = maxWeight != null ? maxWeight : 1.0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getLookback() {
        return lookback;
    }

    public void setLookback(String lookback) {
        this.lookback = lookback;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public static class Builder {
        private String method = "MAX_SHARPE";
        private String lookback = "3Y";
        private Double maxWeight = 1.0;

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder lookback(String lookback) {
            this.lookback = lookback;
            return this;
        }

        public Builder maxWeight(Double maxWeight) {
            this.maxWeight = maxWeight;
            return this;
        }

        public OptimizationApiRequest build() {
            return new OptimizationApiRequest(method, lookback, maxWeight);
        }
    }
}
