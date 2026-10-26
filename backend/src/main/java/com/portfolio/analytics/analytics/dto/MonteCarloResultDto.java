package com.portfolio.analytics.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class MonteCarloResultDto {

    private int simulations;

    @JsonProperty("time_horizon_days")
    private int horizonDays;

    @JsonProperty("initial_portfolio_value")
    private Double initialValue;

    @JsonProperty("mean_ending_value")
    private Double meanEndingValue;

    @JsonProperty("median_ending_value")
    private Double medianEndingValue;

    @JsonProperty("probability_of_loss")
    private Double probabilityOfLoss;

    private Map<String, Double> percentiles;

    public MonteCarloResultDto() {
    }

    public MonteCarloResultDto(int simulations, int horizonDays, Double initialValue,
                              Double meanEndingValue, Double medianEndingValue,
                              Double probabilityOfLoss, Map<String, Double> percentiles) {
        this.simulations = simulations;
        this.horizonDays = horizonDays;
        this.initialValue = initialValue;
        this.meanEndingValue = meanEndingValue;
        this.medianEndingValue = medianEndingValue;
        this.probabilityOfLoss = probabilityOfLoss;
        this.percentiles = percentiles;
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

    public int getHorizonDays() {
        return horizonDays;
    }

    public void setHorizonDays(int horizonDays) {
        this.horizonDays = horizonDays;
    }

    public Double getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Double initialValue) {
        this.initialValue = initialValue;
    }

    public Double getMeanEndingValue() {
        return meanEndingValue;
    }

    public void setMeanEndingValue(Double meanEndingValue) {
        this.meanEndingValue = meanEndingValue;
    }

    public Double getMedianEndingValue() {
        return medianEndingValue;
    }

    public void setMedianEndingValue(Double medianEndingValue) {
        this.medianEndingValue = medianEndingValue;
    }

    public Double getProbabilityOfLoss() {
        return probabilityOfLoss;
    }

    public void setProbabilityOfLoss(Double probabilityOfLoss) {
        this.probabilityOfLoss = probabilityOfLoss;
    }

    public Map<String, Double> getPercentiles() {
        return percentiles;
    }

    public void setPercentiles(Map<String, Double> percentiles) {
        this.percentiles = percentiles;
    }

    public static class Builder {
        private int simulations;
        private int horizonDays;
        private Double initialValue;
        private Double meanEndingValue;
        private Double medianEndingValue;
        private Double probabilityOfLoss;
        private Map<String, Double> percentiles;

        public Builder simulations(int simulations) {
            this.simulations = simulations;
            return this;
        }

        public Builder horizonDays(int horizonDays) {
            this.horizonDays = horizonDays;
            return this;
        }

        public Builder initialValue(Double initialValue) {
            this.initialValue = initialValue;
            return this;
        }

        public Builder meanEndingValue(Double meanEndingValue) {
            this.meanEndingValue = meanEndingValue;
            return this;
        }

        public Builder medianEndingValue(Double medianEndingValue) {
            this.medianEndingValue = medianEndingValue;
            return this;
        }

        public Builder probabilityOfLoss(Double probabilityOfLoss) {
            this.probabilityOfLoss = probabilityOfLoss;
            return this;
        }

        public Builder percentiles(Map<String, Double> percentiles) {
            this.percentiles = percentiles;
            return this;
        }

        public MonteCarloResultDto build() {
            return new MonteCarloResultDto(simulations, horizonDays, initialValue,
                    meanEndingValue, medianEndingValue, probabilityOfLoss, percentiles);
        }
    }
}
