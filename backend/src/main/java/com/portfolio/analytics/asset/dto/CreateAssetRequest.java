package com.portfolio.analytics.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateAssetRequest {

    @NotBlank(message = "Symbol is required")
    @Size(min = 1, max = 32, message = "Symbol must be between 1 and 32 characters")
    private String symbol;

    private String providerSymbol;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Asset class is required")
    private String assetClass;

    private String sector;

    private String currency = "INR";

    private String exchange;
    private String country;

    public CreateAssetRequest() {
    }

    public CreateAssetRequest(String symbol, String providerSymbol, String name, String assetClass,
                              String sector, String currency, String exchange, String country) {
        this.symbol = symbol;
        this.providerSymbol = providerSymbol;
        this.name = name;
        this.assetClass = assetClass;
        this.sector = sector;
        this.currency = currency != null ? currency : "INR";
        this.exchange = exchange;
        this.country = country;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getProviderSymbol() {
        return providerSymbol;
    }

    public void setProviderSymbol(String providerSymbol) {
        this.providerSymbol = providerSymbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static class Builder {
        private String symbol;
        private String providerSymbol;
        private String name;
        private String assetClass;
        private String sector;
        private String currency = "INR";
        private String exchange;
        private String country;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder providerSymbol(String providerSymbol) {
            this.providerSymbol = providerSymbol;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder assetClass(String assetClass) {
            this.assetClass = assetClass;
            return this;
        }

        public Builder sector(String sector) {
            this.sector = sector;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder exchange(String exchange) {
            this.exchange = exchange;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public CreateAssetRequest build() {
            return new CreateAssetRequest(symbol, providerSymbol, name, assetClass, sector, currency, exchange, country);
        }
    }
}
