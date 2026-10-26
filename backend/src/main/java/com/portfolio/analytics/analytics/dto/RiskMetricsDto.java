package com.portfolio.analytics.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RiskMetricsDto {

    @JsonProperty("daily_volatility")
    private Double dailyVolatility;

    @JsonProperty("annualized_volatility")
    private Double annualizedVolatility;

    @JsonProperty("downside_deviation")
    private Double downsideDeviation;

    @JsonProperty("sharpe_ratio")
    private Double sharpeRatio;

    @JsonProperty("sortino_ratio")
    private Double sortinoRatio;

    private Double beta;
    private Double alpha;

    @JsonProperty("historical_var_95")
    private Double historicalVar95;

    @JsonProperty("historical_var_99")
    private Double historicalVar99;

    @JsonProperty("parametric_var_95")
    private Double parametricVar95;

    @JsonProperty("parametric_var_99")
    private Double parametricVar99;

    @JsonProperty("expected_shortfall_95")
    private Double expectedShortfall95;

    @JsonProperty("expected_shortfall_99")
    private Double expectedShortfall99;

    @JsonProperty("max_drawdown")
    private Double maxDrawdown;

    @JsonProperty("dollar_var_95")
    private Double dollarVar95;

    @JsonProperty("dollar_expected_shortfall_95")
    private Double dollarExpectedShortfall95;

    public RiskMetricsDto() {
    }

    public RiskMetricsDto(Double dailyVolatility, Double annualizedVolatility, Double downsideDeviation,
                          Double sharpeRatio, Double sortinoRatio, Double beta, Double alpha,
                          Double historicalVar95, Double historicalVar99, Double parametricVar95,
                          Double parametricVar99, Double expectedShortfall95, Double expectedShortfall99,
                          Double maxDrawdown, Double dollarVar95, Double dollarExpectedShortfall95) {
        this.dailyVolatility = dailyVolatility;
        this.annualizedVolatility = annualizedVolatility;
        this.downsideDeviation = downsideDeviation;
        this.sharpeRatio = sharpeRatio;
        this.sortinoRatio = sortinoRatio;
        this.beta = beta;
        this.alpha = alpha;
        this.historicalVar95 = historicalVar95;
        this.historicalVar99 = historicalVar99;
        this.parametricVar95 = parametricVar95;
        this.parametricVar99 = parametricVar99;
        this.expectedShortfall95 = expectedShortfall95;
        this.expectedShortfall99 = expectedShortfall99;
        this.maxDrawdown = maxDrawdown;
        this.dollarVar95 = dollarVar95;
        this.dollarExpectedShortfall95 = dollarExpectedShortfall95;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Double getDailyVolatility() {
        return dailyVolatility;
    }

    public void setDailyVolatility(Double dailyVolatility) {
        this.dailyVolatility = dailyVolatility;
    }

    public Double getAnnualizedVolatility() {
        return annualizedVolatility;
    }

    public void setAnnualizedVolatility(Double annualizedVolatility) {
        this.annualizedVolatility = annualizedVolatility;
    }

    public Double getDownsideDeviation() {
        return downsideDeviation;
    }

    public void setDownsideDeviation(Double downsideDeviation) {
        this.downsideDeviation = downsideDeviation;
    }

    public Double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public Double getSortinoRatio() {
        return sortinoRatio;
    }

    public void setSortinoRatio(Double sortinoRatio) {
        this.sortinoRatio = sortinoRatio;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getHistoricalVar95() {
        return historicalVar95;
    }

    public void setHistoricalVar95(Double historicalVar95) {
        this.historicalVar95 = historicalVar95;
    }

    public Double getHistoricalVar99() {
        return historicalVar99;
    }

    public void setHistoricalVar99(Double historicalVar99) {
        this.historicalVar99 = historicalVar99;
    }

    public Double getParametricVar95() {
        return parametricVar95;
    }

    public void setParametricVar95(Double parametricVar95) {
        this.parametricVar95 = parametricVar95;
    }

    public Double getParametricVar99() {
        return parametricVar99;
    }

    public void setParametricVar99(Double parametricVar99) {
        this.parametricVar99 = parametricVar99;
    }

    public Double getExpectedShortfall95() {
        return expectedShortfall95;
    }

    public void setExpectedShortfall95(Double expectedShortfall95) {
        this.expectedShortfall95 = expectedShortfall95;
    }

    public Double getExpectedShortfall99() {
        return expectedShortfall99;
    }

    public void setExpectedShortfall99(Double expectedShortfall99) {
        this.expectedShortfall99 = expectedShortfall99;
    }

    public Double getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(Double maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public Double getDollarVar95() {
        return dollarVar95;
    }

    public void setDollarVar95(Double dollarVar95) {
        this.dollarVar95 = dollarVar95;
    }

    public Double getDollarExpectedShortfall95() {
        return dollarExpectedShortfall95;
    }

    public void setDollarExpectedShortfall95(Double dollarExpectedShortfall95) {
        this.dollarExpectedShortfall95 = dollarExpectedShortfall95;
    }

    public static class Builder {
        private Double dailyVolatility;
        private Double annualizedVolatility;
        private Double downsideDeviation;
        private Double sharpeRatio;
        private Double sortinoRatio;
        private Double beta;
        private Double alpha;
        private Double historicalVar95;
        private Double historicalVar99;
        private Double parametricVar95;
        private Double parametricVar99;
        private Double expectedShortfall95;
        private Double expectedShortfall99;
        private Double maxDrawdown;
        private Double dollarVar95;
        private Double dollarExpectedShortfall95;

        public Builder dailyVolatility(Double dailyVolatility) {
            this.dailyVolatility = dailyVolatility;
            return this;
        }

        public Builder annualizedVolatility(Double annualizedVolatility) {
            this.annualizedVolatility = annualizedVolatility;
            return this;
        }

        public Builder downsideDeviation(Double downsideDeviation) {
            this.downsideDeviation = downsideDeviation;
            return this;
        }

        public Builder sharpeRatio(Double sharpeRatio) {
            this.sharpeRatio = sharpeRatio;
            return this;
        }

        public Builder sortinoRatio(Double sortinoRatio) {
            this.sortinoRatio = sortinoRatio;
            return this;
        }

        public Builder beta(Double beta) {
            this.beta = beta;
            return this;
        }

        public Builder alpha(Double alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder historicalVar95(Double historicalVar95) {
            this.historicalVar95 = historicalVar95;
            return this;
        }

        public Builder historicalVar99(Double historicalVar99) {
            this.historicalVar99 = historicalVar99;
            return this;
        }

        public Builder parametricVar95(Double parametricVar95) {
            this.parametricVar95 = parametricVar95;
            return this;
        }

        public Builder parametricVar99(Double parametricVar99) {
            this.parametricVar99 = parametricVar99;
            return this;
        }

        public Builder expectedShortfall95(Double expectedShortfall95) {
            this.expectedShortfall95 = expectedShortfall95;
            return this;
        }

        public Builder expectedShortfall99(Double expectedShortfall99) {
            this.expectedShortfall99 = expectedShortfall99;
            return this;
        }

        public Builder maxDrawdown(Double maxDrawdown) {
            this.maxDrawdown = maxDrawdown;
            return this;
        }

        public Builder dollarVar95(Double dollarVar95) {
            this.dollarVar95 = dollarVar95;
            return this;
        }

        public Builder dollarExpectedShortfall95(Double dollarExpectedShortfall95) {
            this.dollarExpectedShortfall95 = dollarExpectedShortfall95;
            return this;
        }

        public RiskMetricsDto build() {
            return new RiskMetricsDto(dailyVolatility, annualizedVolatility, downsideDeviation,
                    sharpeRatio, sortinoRatio, beta, alpha, historicalVar95, historicalVar99,
                    parametricVar95, parametricVar99, expectedShortfall95, expectedShortfall99,
                    maxDrawdown, dollarVar95, dollarExpectedShortfall95);
        }
    }
}
