package com.portfolio.analytics.optimization.dto;

import com.portfolio.analytics.analytics.dto.OptimizationResultDto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OptimizationApiResponse {

    private String method;
    private Double expectedReturn;
    private Double expectedVolatility;
    private Double sharpeRatio;
    private List<AssetWeightDto> weights;
    private String calculatedAt;

    public OptimizationApiResponse() {
    }

    public OptimizationApiResponse(String method, Double expectedReturn, Double expectedVolatility,
                                   Double sharpeRatio, List<AssetWeightDto> weights, String calculatedAt) {
        this.method = method;
        this.expectedReturn = expectedReturn;
        this.expectedVolatility = expectedVolatility;
        this.sharpeRatio = sharpeRatio;
        this.weights = weights;
        this.calculatedAt = calculatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static OptimizationApiResponse fromResult(OptimizationResultDto result) {
        List<AssetWeightDto> weightList = new ArrayList<>();
        if (result.getWeights() != null) {
            result.getWeights().forEach((k, v) -> weightList.add(new AssetWeightDto(k, v)));
        }

        return builder()
                .method(result.getMethod())
                .expectedReturn(result.getExpectedAnnualReturn())
                .expectedVolatility(result.getExpectedAnnualVolatility())
                .sharpeRatio(result.getSharpeRatio())
                .weights(weightList)
                .calculatedAt(result.getCalculatedAt() != null ? result.getCalculatedAt() : Instant.now().toString())
                .build();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Double getExpectedReturn() {
        return expectedReturn;
    }

    public void setExpectedReturn(Double expectedReturn) {
        this.expectedReturn = expectedReturn;
    }

    public Double getExpectedVolatility() {
        return expectedVolatility;
    }

    public void setExpectedVolatility(Double expectedVolatility) {
        this.expectedVolatility = expectedVolatility;
    }

    public Double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public List<AssetWeightDto> getWeights() {
        return weights;
    }

    public void setWeights(List<AssetWeightDto> weights) {
        this.weights = weights;
    }

    public String getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(String calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public static class Builder {
        private String method;
        private Double expectedReturn;
        private Double expectedVolatility;
        private Double sharpeRatio;
        private List<AssetWeightDto> weights;
        private String calculatedAt;

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder expectedReturn(Double expectedReturn) {
            this.expectedReturn = expectedReturn;
            return this;
        }

        public Builder expectedVolatility(Double expectedVolatility) {
            this.expectedVolatility = expectedVolatility;
            return this;
        }

        public Builder sharpeRatio(Double sharpeRatio) {
            this.sharpeRatio = sharpeRatio;
            return this;
        }

        public Builder weights(List<AssetWeightDto> weights) {
            this.weights = weights;
            return this;
        }

        public Builder calculatedAt(String calculatedAt) {
            this.calculatedAt = calculatedAt;
            return this;
        }

        public OptimizationApiResponse build() {
            return new OptimizationApiResponse(method, expectedReturn, expectedVolatility, sharpeRatio, weights, calculatedAt);
        }
    }

    public static class AssetWeightDto {
        private String symbol;
        private Double weight;

        public AssetWeightDto() {
        }

        public AssetWeightDto(String symbol, Double weight) {
            this.symbol = symbol;
            this.weight = weight;
        }

        public static AssetWeightDtoBuilder builder() {
            return new AssetWeightDtoBuilder();
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public static class AssetWeightDtoBuilder {
            private String symbol;
            private Double weight;

            public AssetWeightDtoBuilder symbol(String symbol) {
                this.symbol = symbol;
                return this;
            }

            public AssetWeightDtoBuilder weight(Double weight) {
                this.weight = weight;
                return this;
            }

            public AssetWeightDto build() {
                return new AssetWeightDto(symbol, weight);
            }
        }
    }
}
