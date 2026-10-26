package com.portfolio.analytics.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PerformanceMetricsDto {

    @JsonProperty("total_return")
    private Double totalReturn;

    private Double cagr;

    @JsonProperty("annualized_return")
    private Double annualizedReturn;

    @JsonProperty("benchmark_return")
    private Double benchmarkReturn;

    @JsonProperty("excess_return")
    private Double excessReturn;

    @JsonProperty("tracking_error")
    private Double trackingError;

    @JsonProperty("information_ratio")
    private Double informationRatio;

    @JsonProperty("best_day_return")
    private Double bestDayReturn;

    @JsonProperty("worst_day_return")
    private Double worstDayReturn;

    @JsonProperty("positive_trading_days_ratio")
    private Double positiveTradingDaysRatio;

    public PerformanceMetricsDto() {
    }

    public PerformanceMetricsDto(Double totalReturn, Double cagr, Double annualizedReturn,
                                 Double benchmarkReturn, Double excessReturn, Double trackingError,
                                 Double informationRatio, Double bestDayReturn, Double worstDayReturn,
                                 Double positiveTradingDaysRatio) {
        this.totalReturn = totalReturn;
        this.cagr = cagr;
        this.annualizedReturn = annualizedReturn;
        this.benchmarkReturn = benchmarkReturn;
        this.excessReturn = excessReturn;
        this.trackingError = trackingError;
        this.informationRatio = informationRatio;
        this.bestDayReturn = bestDayReturn;
        this.worstDayReturn = worstDayReturn;
        this.positiveTradingDaysRatio = positiveTradingDaysRatio;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Double getTotalReturn() {
        return totalReturn;
    }

    public void setTotalReturn(Double totalReturn) {
        this.totalReturn = totalReturn;
    }

    public Double getCagr() {
        return cagr;
    }

    public void setCagr(Double cagr) {
        this.cagr = cagr;
    }

    public Double getAnnualizedReturn() {
        return annualizedReturn;
    }

    public void setAnnualizedReturn(Double annualizedReturn) {
        this.annualizedReturn = annualizedReturn;
    }

    public Double getBenchmarkReturn() {
        return benchmarkReturn;
    }

    public void setBenchmarkReturn(Double benchmarkReturn) {
        this.benchmarkReturn = benchmarkReturn;
    }

    public Double getExcessReturn() {
        return excessReturn;
    }

    public void setExcessReturn(Double excessReturn) {
        this.excessReturn = excessReturn;
    }

    public Double getTrackingError() {
        return trackingError;
    }

    public void setTrackingError(Double trackingError) {
        this.trackingError = trackingError;
    }

    public Double getInformationRatio() {
        return informationRatio;
    }

    public void setInformationRatio(Double informationRatio) {
        this.informationRatio = informationRatio;
    }

    public Double getBestDayReturn() {
        return bestDayReturn;
    }

    public void setBestDayReturn(Double bestDayReturn) {
        this.bestDayReturn = bestDayReturn;
    }

    public Double getWorstDayReturn() {
        return worstDayReturn;
    }

    public void setWorstDayReturn(Double worstDayReturn) {
        this.worstDayReturn = worstDayReturn;
    }

    public Double getPositiveTradingDaysRatio() {
        return positiveTradingDaysRatio;
    }

    public void setPositiveTradingDaysRatio(Double positiveTradingDaysRatio) {
        this.positiveTradingDaysRatio = positiveTradingDaysRatio;
    }

    public static class Builder {
        private Double totalReturn;
        private Double cagr;
        private Double annualizedReturn;
        private Double benchmarkReturn;
        private Double excessReturn;
        private Double trackingError;
        private Double informationRatio;
        private Double bestDayReturn;
        private Double worstDayReturn;
        private Double positiveTradingDaysRatio;

        public Builder totalReturn(Double totalReturn) {
            this.totalReturn = totalReturn;
            return this;
        }

        public Builder cagr(Double cagr) {
            this.cagr = cagr;
            return this;
        }

        public Builder annualizedReturn(Double annualizedReturn) {
            this.annualizedReturn = annualizedReturn;
            return this;
        }

        public Builder benchmarkReturn(Double benchmarkReturn) {
            this.benchmarkReturn = benchmarkReturn;
            return this;
        }

        public Builder excessReturn(Double excessReturn) {
            this.excessReturn = excessReturn;
            return this;
        }

        public Builder trackingError(Double trackingError) {
            this.trackingError = trackingError;
            return this;
        }

        public Builder informationRatio(Double informationRatio) {
            this.informationRatio = informationRatio;
            return this;
        }

        public Builder bestDayReturn(Double bestDayReturn) {
            this.bestDayReturn = bestDayReturn;
            return this;
        }

        public Builder worstDayReturn(Double worstDayReturn) {
            this.worstDayReturn = worstDayReturn;
            return this;
        }

        public Builder positiveTradingDaysRatio(Double positiveTradingDaysRatio) {
            this.positiveTradingDaysRatio = positiveTradingDaysRatio;
            return this;
        }

        public PerformanceMetricsDto build() {
            return new PerformanceMetricsDto(totalReturn, cagr, annualizedReturn, benchmarkReturn,
                    excessReturn, trackingError, informationRatio, bestDayReturn, worstDayReturn, positiveTradingDaysRatio);
        }
    }
}
