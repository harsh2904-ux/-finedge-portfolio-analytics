package com.portfolio.analytics.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePortfolioRequest {

    @NotBlank(message = "Portfolio name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    private String baseCurrency = "INR";

    private Long benchmarkAssetId;
    private String benchmarkSymbol;

    public CreatePortfolioRequest() {
    }

    public CreatePortfolioRequest(String name, String baseCurrency, Long benchmarkAssetId, String benchmarkSymbol) {
        this.name = name;
        this.baseCurrency = baseCurrency != null ? baseCurrency : "INR";
        this.benchmarkAssetId = benchmarkAssetId;
        this.benchmarkSymbol = benchmarkSymbol;
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

    public String getBenchmarkSymbol() {
        return benchmarkSymbol;
    }

    public void setBenchmarkSymbol(String benchmarkSymbol) {
        this.benchmarkSymbol = benchmarkSymbol;
    }

    public static class Builder {
        private String name;
        private String baseCurrency = "INR";
        private Long benchmarkAssetId;
        private String benchmarkSymbol;

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

        public Builder benchmarkSymbol(String benchmarkSymbol) {
            this.benchmarkSymbol = benchmarkSymbol;
            return this;
        }

        public CreatePortfolioRequest build() {
            return new CreatePortfolioRequest(name, baseCurrency, benchmarkAssetId, benchmarkSymbol);
        }
    }
}
