package com.portfolio.analytics.holding.dto;

import com.portfolio.analytics.holding.PortfolioHolding;
import java.time.Instant;

public class HoldingResponse {

    private Long id;
    private Long portfolioId;
    private Long assetId;
    private String symbol;
    private String assetName;
    private String assetClass;
    private String currency;
    private Double quantity;
    private Double averageCost;
    private Double targetWeight;
    private Instant purchaseDate;
    private Instant createdAt;
    private Instant updatedAt;

    public HoldingResponse() {
    }

    public HoldingResponse(Long id, Long portfolioId, Long assetId, String symbol,
                           String assetName, String assetClass, String currency,
                           Double quantity, Double averageCost, Double targetWeight,
                           Instant purchaseDate, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.assetId = assetId;
        this.symbol = symbol;
        this.assetName = assetName;
        this.assetClass = assetClass;
        this.currency = currency;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.targetWeight = targetWeight;
        this.purchaseDate = purchaseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static HoldingResponse fromEntity(PortfolioHolding entity) {
        return builder()
                .id(entity.getId())
                .portfolioId(entity.getPortfolio().getId())
                .assetId(entity.getAsset().getId())
                .symbol(entity.getAsset().getSymbol())
                .assetName(entity.getAsset().getName())
                .assetClass(entity.getAsset().getAssetClass())
                .currency(entity.getAsset().getCurrency())
                .quantity(entity.getQuantity())
                .averageCost(entity.getAverageCost())
                .targetWeight(entity.getTargetWeight())
                .purchaseDate(entity.getPurchaseDate())
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

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Double averageCost) {
        this.averageCost = averageCost;
    }

    public Double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
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
        private Long portfolioId;
        private Long assetId;
        private String symbol;
        private String assetName;
        private String assetClass;
        private String currency;
        private Double quantity;
        private Double averageCost;
        private Double targetWeight;
        private Instant purchaseDate;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder portfolioId(Long portfolioId) {
            this.portfolioId = portfolioId;
            return this;
        }

        public Builder assetId(Long assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder assetName(String assetName) {
            this.assetName = assetName;
            return this;
        }

        public Builder assetClass(String assetClass) {
            this.assetClass = assetClass;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder averageCost(Double averageCost) {
            this.averageCost = averageCost;
            return this;
        }

        public Builder targetWeight(Double targetWeight) {
            this.targetWeight = targetWeight;
            return this;
        }

        public Builder purchaseDate(Instant purchaseDate) {
            this.purchaseDate = purchaseDate;
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

        public HoldingResponse build() {
            return new HoldingResponse(id, portfolioId, assetId, symbol, assetName, assetClass, currency,
                    quantity, averageCost, targetWeight, purchaseDate, createdAt, updatedAt);
        }
    }
}
