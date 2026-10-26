package com.portfolio.analytics.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class CorrelationDto {

    private String lookback;
    private List<String> symbols;

    @JsonProperty("correlation_matrix")
    private Map<String, Map<String, Double>> correlationMatrix;

    @JsonProperty("covariance_matrix")
    private Map<String, Map<String, Double>> covarianceMatrix;

    public CorrelationDto() {
    }

    public CorrelationDto(String lookback, List<String> symbols,
                          Map<String, Map<String, Double>> correlationMatrix,
                          Map<String, Map<String, Double>> covarianceMatrix) {
        this.lookback = lookback;
        this.symbols = symbols;
        this.correlationMatrix = correlationMatrix;
        this.covarianceMatrix = covarianceMatrix;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLookback() {
        return lookback;
    }

    public void setLookback(String lookback) {
        this.lookback = lookback;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }

    public Map<String, Map<String, Double>> getCorrelationMatrix() {
        return correlationMatrix;
    }

    public void setCorrelationMatrix(Map<String, Map<String, Double>> correlationMatrix) {
        this.correlationMatrix = correlationMatrix;
    }

    public Map<String, Map<String, Double>> getCovarianceMatrix() {
        return covarianceMatrix;
    }

    public void setCovarianceMatrix(Map<String, Map<String, Double>> covarianceMatrix) {
        this.covarianceMatrix = covarianceMatrix;
    }

    public static class Builder {
        private String lookback;
        private List<String> symbols;
        private Map<String, Map<String, Double>> correlationMatrix;
        private Map<String, Map<String, Double>> covarianceMatrix;

        public Builder lookback(String lookback) {
            this.lookback = lookback;
            return this;
        }

        public Builder symbols(List<String> symbols) {
            this.symbols = symbols;
            return this;
        }

        public Builder correlationMatrix(Map<String, Map<String, Double>> correlationMatrix) {
            this.correlationMatrix = correlationMatrix;
            return this;
        }

        public Builder covarianceMatrix(Map<String, Map<String, Double>> covarianceMatrix) {
            this.covarianceMatrix = covarianceMatrix;
            return this;
        }

        public CorrelationDto build() {
            return new CorrelationDto(lookback, symbols, correlationMatrix, covarianceMatrix);
        }
    }
}
