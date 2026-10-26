package com.portfolio.analytics.market;

import com.portfolio.analytics.asset.Asset;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "market_prices", uniqueConstraints = {
        @UniqueConstraint(name = "uq_asset_time_source", columnNames = {"asset_id", "timestamp", "source"})
})
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private Double open;

    @Column(nullable = false)
    private Double high;

    @Column(nullable = false)
    private Double low;

    @Column(nullable = false)
    private Double close;

    @Column(name = "adjusted_close")
    private Double adjustedClose;

    @Column(nullable = false)
    private Double volume = 0.0;

    @Column(nullable = false, length = 64)
    private String source = "twelve_data";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public MarketPrice() {
    }

    public MarketPrice(Long id, Asset asset, Instant timestamp, Double open, Double high,
                       Double low, Double close, Double adjustedClose, Double volume,
                       String source, Instant createdAt) {
        this.id = id;
        this.asset = asset;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.adjustedClose = adjustedClose;
        this.volume = volume != null ? volume : 0.0;
        this.source = source != null ? source : "twelve_data";
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public Double getOpen() { return open; }
    public void setOpen(Double open) { this.open = open; }

    public Double getHigh() { return high; }
    public void setHigh(Double high) { this.high = high; }

    public Double getLow() { return low; }
    public void setLow(Double low) { this.low = low; }

    public Double getClose() { return close; }
    public void setClose(Double close) { this.close = close; }

    public Double getAdjustedClose() { return adjustedClose; }
    public void setAdjustedClose(Double adjustedClose) { this.adjustedClose = adjustedClose; }

    public Double getVolume() { return volume; }
    public void setVolume(Double volume) { this.volume = volume; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Asset asset;
        private Instant timestamp;
        private Double open;
        private Double high;
        private Double low;
        private Double close;
        private Double adjustedClose;
        private Double volume = 0.0;
        private String source = "twelve_data";
        private Instant createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder asset(Asset asset) { this.asset = asset; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder open(Double open) { this.open = open; return this; }
        public Builder high(Double high) { this.high = high; return this; }
        public Builder low(Double low) { this.low = low; return this; }
        public Builder close(Double close) { this.close = close; return this; }
        public Builder adjustedClose(Double adjustedClose) { this.adjustedClose = adjustedClose; return this; }
        public Builder volume(Double volume) { this.volume = volume; return this; }
        public Builder source(String source) { this.source = source; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public MarketPrice build() {
            return new MarketPrice(id, asset, timestamp, open, high, low, close, adjustedClose, volume, source, createdAt);
        }
    }
}
