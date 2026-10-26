package com.portfolio.analytics.holding;

import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.portfolio.Portfolio;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "portfolio_holdings", uniqueConstraints = {
        @UniqueConstraint(name = "uq_portfolio_asset", columnNames = {"portfolio_id", "asset_id"})
})
public class PortfolioHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "average_cost", nullable = false)
    private Double averageCost;

    @Column(name = "target_weight")
    private Double targetWeight;

    @Column(name = "purchase_date", nullable = false)
    private Instant purchaseDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public PortfolioHolding() {
    }

    public PortfolioHolding(Long id, Portfolio portfolio, Asset asset, Double quantity,
                            Double averageCost, Double targetWeight, Instant purchaseDate,
                            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.portfolio = portfolio;
        this.asset = asset;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.targetWeight = targetWeight;
        this.purchaseDate = purchaseDate != null ? purchaseDate : Instant.now();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.purchaseDate == null) {
            this.purchaseDate = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Portfolio getPortfolio() { return portfolio; }
    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getAverageCost() { return averageCost; }
    public void setAverageCost(Double averageCost) { this.averageCost = averageCost; }

    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double targetWeight) { this.targetWeight = targetWeight; }

    public Instant getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Instant purchaseDate) { this.purchaseDate = purchaseDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Portfolio portfolio;
        private Asset asset;
        private Double quantity;
        private Double averageCost;
        private Double targetWeight;
        private Instant purchaseDate;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder portfolio(Portfolio portfolio) { this.portfolio = portfolio; return this; }
        public Builder asset(Asset asset) { this.asset = asset; return this; }
        public Builder quantity(Double quantity) { this.quantity = quantity; return this; }
        public Builder averageCost(Double averageCost) { this.averageCost = averageCost; return this; }
        public Builder targetWeight(Double targetWeight) { this.targetWeight = targetWeight; return this; }
        public Builder purchaseDate(Instant purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public PortfolioHolding build() {
            return new PortfolioHolding(id, portfolio, asset, quantity, averageCost, targetWeight, purchaseDate, createdAt, updatedAt);
        }
    }
}
