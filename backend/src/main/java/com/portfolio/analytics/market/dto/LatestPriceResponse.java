package com.portfolio.analytics.market.dto;

import java.time.Instant;

public class LatestPriceResponse {

    private String symbol;
    private String name;
    private String currency;
    private Double price;
    private Double change;
    private Double changePercent;
    private Instant asOf;

    public LatestPriceResponse() {
    }

    public LatestPriceResponse(String symbol, String name, String currency, Double price,
                               Double change, Double changePercent, Instant asOf) {
        this.symbol = symbol;
        this.name = name;
        this.currency = currency;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
        this.asOf = asOf;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public Double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(Double changePercent) {
        this.changePercent = changePercent;
    }

    public Instant getAsOf() {
        return asOf;
    }

    public void setAsOf(Instant asOf) {
        this.asOf = asOf;
    }

    public static class Builder {
        private String symbol;
        private String name;
        private String currency;
        private Double price;
        private Double change;
        private Double changePercent;
        private Instant asOf;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder change(Double change) {
            this.change = change;
            return this;
        }

        public Builder changePercent(Double changePercent) {
            this.changePercent = changePercent;
            return this;
        }

        public Builder asOf(Instant asOf) {
            this.asOf = asOf;
            return this;
        }

        public LatestPriceResponse build() {
            return new LatestPriceResponse(symbol, name, currency, price, change, changePercent, asOf);
        }
    }
}
