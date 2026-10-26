package com.portfolio.analytics.asset.dto;

public class UpdateAssetRequest {

    private String name;
    private String assetClass;
    private String sector;
    private String currency;
    private String exchange;
    private String country;
    private Boolean isActive;

    public UpdateAssetRequest() {
    }

    public UpdateAssetRequest(String name, String assetClass, String sector, String currency,
                              String exchange, String country, Boolean isActive) {
        this.name = name;
        this.assetClass = assetClass;
        this.sector = sector;
        this.currency = currency;
        this.exchange = exchange;
        this.country = country;
        this.isActive = isActive;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public static class Builder {
        private String name;
        private String assetClass;
        private String sector;
        private String currency;
        private String exchange;
        private String country;
        private Boolean isActive;

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

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UpdateAssetRequest build() {
            return new UpdateAssetRequest(name, assetClass, sector, currency, exchange, country, isActive);
        }
    }
}
