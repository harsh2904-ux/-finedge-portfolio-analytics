package com.portfolio.analytics.portfolio;

import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.holding.PortfolioHolding;
import com.portfolio.analytics.transaction.Transaction;
import com.portfolio.analytics.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "base_currency", nullable = false, length = 8)
    private String baseCurrency = "INR";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benchmark_asset_id")
    private Asset benchmarkAsset;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioHolding> holdings = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Portfolio() {
    }

    public Portfolio(Long id, User user, String name, String baseCurrency, Asset benchmarkAsset,
                     List<PortfolioHolding> holdings, List<Transaction> transactions,
                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.baseCurrency = baseCurrency != null ? baseCurrency : "INR";
        this.benchmarkAsset = benchmarkAsset;
        this.holdings = holdings != null ? holdings : new ArrayList<>();
        this.transactions = transactions != null ? transactions : new ArrayList<>();
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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public Asset getBenchmarkAsset() { return benchmarkAsset; }
    public void setBenchmarkAsset(Asset benchmarkAsset) { this.benchmarkAsset = benchmarkAsset; }

    public List<PortfolioHolding> getHoldings() { return holdings; }
    public void setHoldings(List<PortfolioHolding> holdings) { this.holdings = holdings; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private User user;
        private String name;
        private String baseCurrency = "INR";
        private Asset benchmarkAsset;
        private List<PortfolioHolding> holdings = new ArrayList<>();
        private List<Transaction> transactions = new ArrayList<>();
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder baseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; return this; }
        public Builder benchmarkAsset(Asset benchmarkAsset) { this.benchmarkAsset = benchmarkAsset; return this; }
        public Builder holdings(List<PortfolioHolding> holdings) { this.holdings = holdings; return this; }
        public Builder transactions(List<Transaction> transactions) { this.transactions = transactions; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public Portfolio build() {
            return new Portfolio(id, user, name, baseCurrency, benchmarkAsset, holdings, transactions, createdAt, updatedAt);
        }
    }
}
