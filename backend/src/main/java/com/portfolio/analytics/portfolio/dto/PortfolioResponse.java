package com.portfolio.analytics.portfolio.dto;

import com.portfolio.analytics.portfolio.Portfolio;
import java.time.Instant;

public class PortfolioResponse {

    private Long id;
    private Long userId;
    private String name;
    private String baseCurrency;
    private Long benchmarkAssetId;
    private String benchmarkSymbol;
    private int holdingsCount;
    private Instant createdAt;
    private Instant updatedAt;

    public PortfolioResponse() {
    }

    public PortfolioResponse(Long id, Long userId, String name, String baseCurrency,
                             Long benchmarkAssetId, String benchmarkSymbol, int holdingsCount,
                             Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.baseCurrency = baseCurrency;
        this.benchmarkAssetId = benchmarkAssetId;
        this.benchmarkSymbol = benchmarkSymbol;
        this.holdingsCount = holdingsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PortfolioResponse fromEntity(Portfolio entity) {
        return builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .name(entity.getName())
                .baseCurrency(entity.getBaseCurrency())
                .benchmarkAssetId(entity.getBenchmarkAsset() != null ? entity.getBenchmarkAsset().getId() : null)
                .benchmarkSymbol(entity.getBenchmarkAsset() != null ? entity.getBenchmarkAsset().getSymbol() : null)
                .holdingsCount(entity.getHoldings() != null ? entity.getHoldings().size() : 0)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public int getHoldingsCount() {
        return holdingsCount;
    }

    public void setHoldingsCount(int holdingsCount) {
        this.holdingsCount = holdingsCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String name;
        private String baseCurrency;
        private Long benchmarkAssetId;
        private String benchmarkSymbol;
        private int holdingsCount;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

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

        public Builder holdingsCount(int holdingsCount) {
            this.holdingsCount = holdingsCount;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PortfolioResponse build() {
            return new PortfolioResponse(id, userId, name, baseCurrency, benchmarkAssetId, benchmarkSymbol, holdingsCount, createdAt, updatedAt);
        }
    }
}
