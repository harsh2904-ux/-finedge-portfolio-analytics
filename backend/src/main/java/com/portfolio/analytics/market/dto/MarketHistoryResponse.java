package com.portfolio.analytics.market.dto;

import java.util.List;

public class MarketHistoryResponse {

    private String symbol;
    private String currency;
    private int totalRecords;
    private List<MarketPriceDto> data;

    public MarketHistoryResponse() {
    }

    public MarketHistoryResponse(String symbol, String currency, int totalRecords, List<MarketPriceDto> data) {
        this.symbol = symbol;
        this.currency = currency;
        this.totalRecords = totalRecords;
        this.data = data;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<MarketPriceDto> getData() {
        return data;
    }

    public void setData(List<MarketPriceDto> data) {
        this.data = data;
    }

    public static class Builder {
        private String symbol;
        private String currency;
        private int totalRecords;
        private List<MarketPriceDto> data;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder totalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
            return this;
        }

        public Builder data(List<MarketPriceDto> data) {
            this.data = data;
            return this;
        }

        public MarketHistoryResponse build() {
            return new MarketHistoryResponse(symbol, currency, totalRecords, data);
        }
    }
}
