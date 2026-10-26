package com.portfolio.analytics.market.dto;

import com.portfolio.analytics.market.MarketPrice;
import java.time.Instant;

public class MarketPriceDto {

    private Instant timestamp;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double adjustedClose;
    private Double volume;

    public MarketPriceDto() {
    }

    public MarketPriceDto(Instant timestamp, Double open, Double high, Double low,
                          Double close, Double adjustedClose, Double volume) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.adjustedClose = adjustedClose;
        this.volume = volume;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MarketPriceDto fromEntity(MarketPrice entity) {
        return builder()
                .timestamp(entity.getTimestamp())
                .open(entity.getOpen())
                .high(entity.getHigh())
                .low(entity.getLow())
                .close(entity.getClose())
                .adjustedClose(entity.getAdjustedClose())
                .volume(entity.getVolume())
                .build();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getAdjustedClose() {
        return adjustedClose;
    }

    public void setAdjustedClose(Double adjustedClose) {
        this.adjustedClose = adjustedClose;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public static class Builder {
        private Instant timestamp;
        private Double open;
        private Double high;
        private Double low;
        private Double close;
        private Double adjustedClose;
        private Double volume;

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder open(Double open) {
            this.open = open;
            return this;
        }

        public Builder high(Double high) {
            this.high = high;
            return this;
        }

        public Builder low(Double low) {
            this.low = low;
            return this;
        }

        public Builder close(Double close) {
            this.close = close;
            return this;
        }

        public Builder adjustedClose(Double adjustedClose) {
            this.adjustedClose = adjustedClose;
            return this;
        }

        public Builder volume(Double volume) {
            this.volume = volume;
            return this;
        }

        public MarketPriceDto build() {
            return new MarketPriceDto(timestamp, open, high, low, close, adjustedClose, volume);
        }
    }
}
