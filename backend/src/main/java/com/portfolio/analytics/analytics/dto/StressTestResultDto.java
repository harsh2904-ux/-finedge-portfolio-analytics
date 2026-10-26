package com.portfolio.analytics.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class StressTestResultDto {

    @JsonProperty("scenario_name")
    private String scenarioName;

    private String description;

    @JsonProperty("portfolio_pnl")
    private Double portfolioPnl;

    @JsonProperty("portfolio_return")
    private Double portfolioReturn;

    @JsonProperty("stressed_portfolio_value")
    private Double stressedPortfolioValue;

    @JsonProperty("asset_shocks")
    private Map<String, Double> assetShocks;

    public StressTestResultDto() {
    }

    public StressTestResultDto(String scenarioName, String description, Double portfolioPnl,
                               Double portfolioReturn, Double stressedPortfolioValue,
                               Map<String, Double> assetShocks) {
        this.scenarioName = scenarioName;
        this.description = description;
        this.portfolioPnl = portfolioPnl;
        this.portfolioReturn = portfolioReturn;
        this.stressedPortfolioValue = stressedPortfolioValue;
        this.assetShocks = assetShocks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPortfolioPnl() {
        return portfolioPnl;
    }

    public void setPortfolioPnl(Double portfolioPnl) {
        this.portfolioPnl = portfolioPnl;
    }

    public Double getPortfolioReturn() {
        return portfolioReturn;
    }

    public void setPortfolioReturn(Double portfolioReturn) {
        this.portfolioReturn = portfolioReturn;
    }

    public Double getStressedPortfolioValue() {
        return stressedPortfolioValue;
    }

    public void setStressedPortfolioValue(Double stressedPortfolioValue) {
        this.stressedPortfolioValue = stressedPortfolioValue;
    }

    public Map<String, Double> getAssetShocks() {
        return assetShocks;
    }

    public void setAssetShocks(Map<String, Double> assetShocks) {
        this.assetShocks = assetShocks;
    }

    public static class Builder {
        private String scenarioName;
        private String description;
        private Double portfolioPnl;
        private Double portfolioReturn;
        private Double stressedPortfolioValue;
        private Map<String, Double> assetShocks;

        public Builder scenarioName(String scenarioName) {
            this.scenarioName = scenarioName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder portfolioPnl(Double portfolioPnl) {
            this.portfolioPnl = portfolioPnl;
            return this;
        }

        public Builder portfolioReturn(Double portfolioReturn) {
            this.portfolioReturn = portfolioReturn;
            return this;
        }

        public Builder stressedPortfolioValue(Double stressedPortfolioValue) {
            this.stressedPortfolioValue = stressedPortfolioValue;
            return this;
        }

        public Builder assetShocks(Map<String, Double> assetShocks) {
            this.assetShocks = assetShocks;
            return this;
        }

        public StressTestResultDto build() {
            return new StressTestResultDto(scenarioName, description, portfolioPnl,
                    portfolioReturn, stressedPortfolioValue, assetShocks);
        }
    }
}
