package com.portfolio.analytics.asset;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String symbol;

    @Column(name = "provider_symbol", nullable = false, length = 64)
    private String providerSymbol;

    @Column(nullable = false)
    private String name;

    @Column(name = "asset_class", nullable = false, length = 64)
    private String assetClass;

    @Column(length = 128)
    private String sector;

    @Column(nullable = false, length = 8)
    private String currency = "INR";

    @Column(length = 64)
    private String exchange;

    @Column(length = 64)
    private String country;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Asset() {
    }

    public Asset(Long id, String symbol, String providerSymbol, String name, String assetClass,
                 String sector, String currency, String exchange, String country, boolean isActive,
                 Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.symbol = symbol;
        this.providerSymbol = providerSymbol;
        this.name = name;
        this.assetClass = assetClass;
        this.sector = sector;
        this.currency = currency != null ? currency : "INR";
        this.exchange = exchange;
        this.country = country;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getProviderSymbol() { return providerSymbol; }
    public void setProviderSymbol(String providerSymbol) { this.providerSymbol = providerSymbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAssetClass() { return assetClass; }
    public void setAssetClass(String assetClass) { this.assetClass = assetClass; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String symbol;
        private String providerSymbol;
        private String name;
        private String assetClass;
        private String sector;
        private String currency = "INR";
        private String exchange;
        private String country;
        private boolean isActive = true;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder symbol(String symbol) { this.symbol = symbol; return this; }
        public Builder providerSymbol(String providerSymbol) { this.providerSymbol = providerSymbol; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder assetClass(String assetClass) { this.assetClass = assetClass; return this; }
        public Builder sector(String sector) { this.sector = sector; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder exchange(String exchange) { this.exchange = exchange; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public Asset build() {
            return new Asset(id, symbol, providerSymbol, name, assetClass, sector, currency, exchange, country, isActive, createdAt, updatedAt);
        }
    }
}
