package com.portfolio.analytics.simulation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class MonteCarloApiRequest {

    @Min(value = 100, message = "Minimum simulations is 100")
    @Max(value = 50000, message = "Maximum simulations is 50000")
    @Schema(description = "Number of stochastic path trajectories", example = "5000")
    private Integer simulations = 5000;

    @Min(value = 5, message = "Minimum horizon days is 5")
    @Max(value = 2520, message = "Maximum horizon days is 2520 (10 years)")
    @Schema(description = "Forward time horizon in trading days (252 = 1 year)", example = "252")
    private Integer horizonDays = 252;

    public MonteCarloApiRequest() {
    }

    public MonteCarloApiRequest(Integer simulations, Integer horizonDays) {
        this.simulations = simulations != null ? simulations : 5000;
        this.horizonDays = horizonDays != null ? horizonDays : 252;
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

    public Integer getHorizonDays() {
        return horizonDays;
    }

    public void setHorizonDays(Integer horizonDays) {
        this.horizonDays = horizonDays;
    }

    public static class Builder {
        private Integer simulations = 5000;
        private Integer horizonDays = 252;

        public Builder simulations(Integer simulations) {
            this.simulations = simulations;
            return this;
        }

        public Builder horizonDays(Integer horizonDays) {
            this.horizonDays = horizonDays;
            return this;
        }

        public MonteCarloApiRequest build() {
            return new MonteCarloApiRequest(simulations, horizonDays);
        }
    }
}
