package com.portfolio.analytics.asset.dto;

import com.portfolio.analytics.asset.Asset;
import java.time.Instant;

public class AssetResponse {

    private Long id;
    private String symbol;
    private String providerSymbol;
    private String name;
    private String assetClass;
    private String sector;
    private String currency;
    private String exchange;
    private String country;
    private boolean active;
    private Instant createdAt;

    public AssetResponse() {
    }

    public AssetResponse(Long id, String symbol, String providerSymbol, String name, String assetClass,
                         String sector, String currency, String exchange, String country,
                         boolean active, Instant createdAt) {
        this.id = id;
        this.symbol = symbol;
        this.providerSymbol = providerSymbol;
        this.name = name;
        this.assetClass = assetClass;
        this.sector = sector;
        this.currency = currency;
        this.exchange = exchange;
        this.country = country;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AssetResponse fromAsset(Asset asset) {
        return builder()
                .id(asset.getId())
                .symbol(asset.getSymbol())
                .providerSymbol(asset.getProviderSymbol())
                .name(asset.getName())
                .assetClass(asset.getAssetClass())
                .sector(asset.getSector())
                .currency(asset.getCurrency())
                .exchange(asset.getExchange())
                .country(asset.getCountry())
                .active(asset.isActive())
                .createdAt(asset.getCreatedAt())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private Long id;
        private String symbol;
        private String providerSymbol;
        private String name;
        private String assetClass;
        private String sector;
        private String currency;
        private String exchange;
        private String country;
        private boolean active;
        private Instant createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AssetResponse build() {
            return new AssetResponse(id, symbol, providerSymbol, name, assetClass, sector, currency, exchange, country, active, createdAt);
        }
    }
}
