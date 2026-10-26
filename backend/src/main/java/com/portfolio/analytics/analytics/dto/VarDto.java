package com.portfolio.analytics.analytics.dto;

public class VarDto {

    private Double confidenceLevel;
    private Double portfolioValue;
    private Double historicalVar;
    private Double parametricVar;
    private Double expectedShortfall;
    private Double dollarHistoricalVar;
    private Double dollarParametricVar;
    private Double dollarExpectedShortfall;

    public VarDto() {
    }

    public VarDto(Double confidenceLevel, Double portfolioValue, Double historicalVar,
                  Double parametricVar, Double expectedShortfall, Double dollarHistoricalVar,
                  Double dollarParametricVar, Double dollarExpectedShortfall) {
        this.confidenceLevel = confidenceLevel;
        this.portfolioValue = portfolioValue;
        this.historicalVar = historicalVar;
        this.parametricVar = parametricVar;
        this.expectedShortfall = expectedShortfall;
        this.dollarHistoricalVar = dollarHistoricalVar;
        this.dollarParametricVar = dollarParametricVar;
        this.dollarExpectedShortfall = dollarExpectedShortfall;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Double getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(Double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public Double getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(Double portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public Double getHistoricalVar() {
        return historicalVar;
    }

    public void setHistoricalVar(Double historicalVar) {
        this.historicalVar = historicalVar;
    }

    public Double getParametricVar() {
        return parametricVar;
    }

    public void setParametricVar(Double parametricVar) {
        this.parametricVar = parametricVar;
    }

    public Double getExpectedShortfall() {
        return expectedShortfall;
    }

    public void setExpectedShortfall(Double expectedShortfall) {
        this.expectedShortfall = expectedShortfall;
    }

    public Double getDollarHistoricalVar() {
        return dollarHistoricalVar;
    }

    public void setDollarHistoricalVar(Double dollarHistoricalVar) {
        this.dollarHistoricalVar = dollarHistoricalVar;
    }

    public Double getDollarParametricVar() {
        return dollarParametricVar;
    }

    public void setDollarParametricVar(Double dollarParametricVar) {
        this.dollarParametricVar = dollarParametricVar;
    }

    public Double getDollarExpectedShortfall() {
        return dollarExpectedShortfall;
    }

    public void setDollarExpectedShortfall(Double dollarExpectedShortfall) {
        this.dollarExpectedShortfall = dollarExpectedShortfall;
    }

    public static class Builder {
        private Double confidenceLevel;
        private Double portfolioValue;
        private Double historicalVar;
        private Double parametricVar;
        private Double expectedShortfall;
        private Double dollarHistoricalVar;
        private Double dollarParametricVar;
        private Double dollarExpectedShortfall;

        public Builder confidenceLevel(Double confidenceLevel) {
            this.confidenceLevel = confidenceLevel;
            return this;
        }

        public Builder portfolioValue(Double portfolioValue) {
            this.portfolioValue = portfolioValue;
            return this;
        }

        public Builder historicalVar(Double historicalVar) {
            this.historicalVar = historicalVar;
            return this;
        }

        public Builder parametricVar(Double parametricVar) {
            this.parametricVar = parametricVar;
            return this;
        }

        public Builder expectedShortfall(Double expectedShortfall) {
            this.expectedShortfall = expectedShortfall;
            return this;
        }

        public Builder dollarHistoricalVar(Double dollarHistoricalVar) {
            this.dollarHistoricalVar = dollarHistoricalVar;
            return this;
        }

        public Builder dollarParametricVar(Double dollarParametricVar) {
            this.dollarParametricVar = dollarParametricVar;
            return this;
        }

        public Builder dollarExpectedShortfall(Double dollarExpectedShortfall) {
            this.dollarExpectedShortfall = dollarExpectedShortfall;
            return this;
        }

        public VarDto build() {
            return new VarDto(confidenceLevel, portfolioValue, historicalVar, parametricVar,
                    expectedShortfall, dollarHistoricalVar, dollarParametricVar, dollarExpectedShortfall);
        }
    }
}
