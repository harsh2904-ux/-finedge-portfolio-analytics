package com.portfolio.analytics.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class EfficientFrontierResultDto {

    private int simulations;
    private List<FrontierPointDto> points;

    @JsonProperty("minimum_volatility_point")
    private FrontierPointDto minimumVolatilityPoint;

    @JsonProperty("maximum_sharpe_point")
    private FrontierPointDto maximumSharpePoint;

    public EfficientFrontierResultDto() {
    }

    public EfficientFrontierResultDto(int simulations, List<FrontierPointDto> points,
                                     FrontierPointDto minimumVolatilityPoint, FrontierPointDto maximumSharpePoint) {
        this.simulations = simulations;
        this.points = points;
        this.minimumVolatilityPoint = minimumVolatilityPoint;
        this.maximumSharpePoint = maximumSharpePoint;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getSimulations() {
        return simulations;
    }

    public void setSimulations(int simulations) {
        this.simulations = simulations;
    }

    public List<FrontierPointDto> getPoints() {
        return points;
    }

    public void setPoints(List<FrontierPointDto> points) {
        this.points = points;
    }

    public FrontierPointDto getMinimumVolatilityPoint() {
        return minimumVolatilityPoint;
    }

    public void setMinimumVolatilityPoint(FrontierPointDto minimumVolatilityPoint) {
        this.minimumVolatilityPoint = minimumVolatilityPoint;
    }

    public FrontierPointDto getMaximumSharpePoint() {
        return maximumSharpePoint;
    }

    public void setMaximumSharpePoint(FrontierPointDto maximumSharpePoint) {
        this.maximumSharpePoint = maximumSharpePoint;
    }

    public static class Builder {
        private int simulations;
        private List<FrontierPointDto> points;
        private FrontierPointDto minimumVolatilityPoint;
        private FrontierPointDto maximumSharpePoint;

        public Builder simulations(int simulations) {
            this.simulations = simulations;
            return this;
        }

        public Builder points(List<FrontierPointDto> points) {
            this.points = points;
            return this;
        }

        public Builder minimumVolatilityPoint(FrontierPointDto minimumVolatilityPoint) {
            this.minimumVolatilityPoint = minimumVolatilityPoint;
            return this;
        }

        public Builder maximumSharpePoint(FrontierPointDto maximumSharpePoint) {
            this.maximumSharpePoint = maximumSharpePoint;
            return this;
        }

        public EfficientFrontierResultDto build() {
            return new EfficientFrontierResultDto(simulations, points, minimumVolatilityPoint, maximumSharpePoint);
        }
    }

    public static class FrontierPointDto {
        @JsonProperty("expected_return")
        private Double expectedReturn;

        private Double volatility;

        @JsonProperty("sharpe_ratio")
        private Double sharpeRatio;

        private Map<String, Double> weights;

        public FrontierPointDto() {
        }

        public FrontierPointDto(Double expectedReturn, Double volatility, Double sharpeRatio, Map<String, Double> weights) {
            this.expectedReturn = expectedReturn;
            this.volatility = volatility;
            this.sharpeRatio = sharpeRatio;
            this.weights = weights;
        }

        public static FrontierPointDtoBuilder builder() {
            return new FrontierPointDtoBuilder();
        }

        public Double getExpectedReturn() {
            return expectedReturn;
        }

        public void setExpectedReturn(Double expectedReturn) {
            this.expectedReturn = expectedReturn;
        }

        public Double getVolatility() {
            return volatility;
        }

        public void setVolatility(Double volatility) {
            this.volatility = volatility;
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

        public static class FrontierPointDtoBuilder {
            private Double expectedReturn;
            private Double volatility;
            private Double sharpeRatio;
            private Map<String, Double> weights;

            public FrontierPointDtoBuilder expectedReturn(Double expectedReturn) {
                this.expectedReturn = expectedReturn;
                return this;
            }

            public FrontierPointDtoBuilder volatility(Double volatility) {
                this.volatility = volatility;
                return this;
            }

            public FrontierPointDtoBuilder sharpeRatio(Double sharpeRatio) {
                this.sharpeRatio = sharpeRatio;
                return this;
            }

            public FrontierPointDtoBuilder weights(Map<String, Double> weights) {
                this.weights = weights;
                return this;
            }

            public FrontierPointDto build() {
                return new FrontierPointDto(expectedReturn, volatility, sharpeRatio, weights);
            }
        }
    }
}
