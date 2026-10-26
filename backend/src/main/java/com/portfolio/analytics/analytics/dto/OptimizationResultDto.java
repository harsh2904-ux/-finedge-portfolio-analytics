package com.portfolio.analytics.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class OptimizationResultDto {

    private String method;

    @JsonProperty("expected_annual_return")
    private Double expectedAnnualReturn;

    @JsonProperty("expected_annual_volatility")
    private Double expectedAnnualVolatility;

    @JsonProperty("sharpe_ratio")
    private Double sharpeRatio;

    private Map<String, Double> weights;

    private String calculatedAt;

    private Map<String, Object> constraints;

    public OptimizationResultDto() {
    }

    public OptimizationResultDto(String method, Double expectedAnnualReturn, Double expectedAnnualVolatility,
                                 Double sharpeRatio, Map<String, Double> weights,
                                 String calculatedAt, Map<String, Object> constraints) {
        this.method = method;
        this.expectedAnnualReturn = expectedAnnualReturn;
        this.expectedAnnualVolatility = expectedAnnualVolatility;
        this.sharpeRatio = sharpeRatio;
        this.weights = weights;
        this.calculatedAt = calculatedAt;
        this.constraints = constraints;
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

    public Double getExpectedAnnualReturn() {
        return expectedAnnualReturn;
    }

    public void setExpectedAnnualReturn(Double expectedAnnualReturn) {
        this.expectedAnnualReturn = expectedAnnualReturn;
    }

    public Double getExpectedAnnualVolatility() {
        return expectedAnnualVolatility;
    }

    public void setExpectedAnnualVolatility(Double expectedAnnualVolatility) {
        this.expectedAnnualVolatility = expectedAnnualVolatility;
    }

    public Double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public Map<String, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<String, Double> weights) {
        this.weights = weights;
    }

    public String getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(String calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public Map<String, Object> getConstraints() {
        return constraints;
    }

    public void setConstraints(Map<String, Object> constraints) {
        this.constraints = constraints;
    }

    public static class Builder {
        private String method;
        private Double expectedAnnualReturn;
        private Double expectedAnnualVolatility;
        private Double sharpeRatio;
        private Map<String, Double> weights;
        private String calculatedAt;
        private Map<String, Object> constraints;

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder expectedAnnualReturn(Double expectedAnnualReturn) {
            this.expectedAnnualReturn = expectedAnnualReturn;
            return this;
        }

        public Builder expectedAnnualVolatility(Double expectedAnnualVolatility) {
            this.expectedAnnualVolatility = expectedAnnualVolatility;
            return this;
        }

        public Builder sharpeRatio(Double sharpeRatio) {
            this.sharpeRatio = sharpeRatio;
            return this;
        }

        public Builder weights(Map<String, Double> weights) {
            this.weights = weights;
            return this;
        }

        public Builder calculatedAt(String calculatedAt) {
            this.calculatedAt = calculatedAt;
            return this;
        }

        public Builder constraints(Map<String, Object> constraints) {
            this.constraints = constraints;
            return this;
        }

        public OptimizationResultDto build() {
            return new OptimizationResultDto(method, expectedAnnualReturn, expectedAnnualVolatility,
                    sharpeRatio, weights, calculatedAt, constraints);
        }
    }
}
