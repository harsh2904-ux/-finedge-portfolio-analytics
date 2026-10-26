package com.portfolio.analytics.portfolio.dto;

public class UpdatePortfolioRequest {

    private String name;
    private String baseCurrency;
    private Long benchmarkAssetId;

    public UpdatePortfolioRequest() {
    }

    public UpdatePortfolioRequest(String name, String baseCurrency, Long benchmarkAssetId) {
        this.name = name;
        this.baseCurrency = baseCurrency;
        this.benchmarkAssetId = benchmarkAssetId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Long getBenchmarkAssetId() {
        return benchmarkAssetId;
    }

    public void setBenchmarkAssetId(Long benchmarkAssetId) {
        this.benchmarkAssetId = benchmarkAssetId;
    }

    public static class Builder {
        private String name;
        private String baseCurrency;
        private Long benchmarkAssetId;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder baseCurrency(String baseCurrency) {
            this.baseCurrency = baseCurrency;
            return this;
        }

        public Builder benchmarkAssetId(Long benchmarkAssetId) {
            this.benchmarkAssetId = benchmarkAssetId;
            return this;
        }

        public UpdatePortfolioRequest build() {
            return new UpdatePortfolioRequest(name, baseCurrency, benchmarkAssetId);
        }
    }
}
