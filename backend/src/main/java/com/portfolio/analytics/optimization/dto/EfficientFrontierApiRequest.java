package com.portfolio.analytics.optimization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class EfficientFrontierApiRequest {

    @Min(value = 100, message = "Minimum simulations is 100")
    @Max(value = 20000, message = "Maximum simulations is 20000")
    @Schema(description = "Number of random portfolio simulations", example = "5000")
    private Integer simulations = 2000;

    @Schema(description = "Historical lookback period: 1Y, 3Y, 5Y, MAX", example = "3Y")
    private String lookback = "3Y";

    public EfficientFrontierApiRequest() {
    }

    public EfficientFrontierApiRequest(Integer simulations, String lookback) {
        this.simulations = simulations != null ? simulations : 2000;
        this.lookback = lookback != null ? lookback : "3Y";
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getSimulations() {
        return simulations;
    }

    public void setSimulations(Integer simulations) {
        this.simulations = simulations;
    }

    public String getLookback() {
        return lookback;
    }

    public void setLookback(String lookback) {
        this.lookback = lookback;
    }

    public static class Builder {
        private Integer simulations = 2000;
        private String lookback = "3Y";

        public Builder simulations(Integer simulations) {
            this.simulations = simulations;
            return this;
        }

        public Builder lookback(String lookback) {
            this.lookback = lookback;
            return this;
        }

        public EfficientFrontierApiRequest build() {
            return new EfficientFrontierApiRequest(simulations, lookback);
        }
    }
}
