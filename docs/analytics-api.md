# Quantitative Analytics REST API Guide

## 1. Overview & Architecture

The Spring Boot backend orchestrates quantitative analysis by securely validating requests, verifying portfolio permissions, extracting relevant holdings and historical prices, and dispatching tasks to the high-performance **Python Analytics Engine (`FastAPI :8001`)**.

Spring Boot caches expensive analytics results in memory (or Redis in clustered mode) with immediate invalidation upon trade execution.

---

## 2. Performance Analytics

### Get Portfolio Performance
`GET /api/portfolios/{id}/performance?lookback=1Y`

**Query Parameters:**
- `lookback`: Time window for calculation (`1M`, `3M`, `6M`, `1Y`, `3Y`, `5Y`, `MAX`). Default: `1Y`.

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "total_return": 0.2450,
    "cagr": 0.2450,
    "annualized_return": 0.2450,
    "benchmark_return": 0.1820,
    "excess_return": 0.0630,
    "tracking_error": 0.0410,
    "information_ratio": 1.5365,
    "best_day_return": 0.0385,
    "worst_day_return": -0.0294,
    "positive_trading_days_ratio": 0.5620
  }
}
```

---

## 3. Risk Analytics

### 3.1 Standard Risk Metrics
`GET /api/portfolios/{id}/risk?lookback=1Y`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "daily_volatility": 0.0095,
    "annualized_volatility": 0.1508,
    "downside_deviation": 0.0980,
    "sharpe_ratio": 1.4250,
    "sortino_ratio": 2.1930,
    "beta": 0.9200,
    "alpha": 0.0480,
    "historical_var_95": 0.0145,
    "historical_var_99": 0.0230,
    "parametric_var_95": 0.0156,
    "parametric_var_99": 0.0221,
    "expected_shortfall_95": 0.0195,
    "expected_shortfall_99": 0.0280,
    "max_drawdown": -0.1420,
    "dollar_var_95": 18187.42,
    "dollar_expected_shortfall_95": 24458.94
  }
}
```

### 3.2 Value at Risk (VaR & CVaR)
`GET /api/portfolios/{id}/risk/var?confidence=0.95`

Provides parametric, historical, and conditional Value at Risk in both percentage and dollar amounts.

### 3.3 Drawdown Analysis
`GET /api/portfolios/{id}/risk/drawdown?lookback=1Y`

Returns maximum drawdown percentage along with historical peak, trough, and recovery dates, plus underwater curve time series.

---

## 4. Diversification & Risk Attribution

### 4.1 Asset Class Allocation
`GET /api/portfolios/{id}/allocation`

Returns weights and market values grouped by asset class (Equity, Fixed Income, Commodities, Cash, Real Estate).

### 4.2 Cross-Asset Correlation & Covariance
`GET /api/portfolios/{id}/correlation?lookback=1Y`

Returns the $N \times N$ Pearson correlation matrix and covariance matrix between all held assets.

### 4.3 Euler Risk Contribution (Risk Decomposition)
`GET /api/portfolios/{id}/risk-contribution?lookback=1Y`

Decomposes total portfolio volatility into marginal and component risk contributions per asset.

---

## 5. Portfolio Optimization & Efficient Frontier

### 5.1 Markowitz Mean-Variance Optimization
`POST /api/portfolios/{id}/optimization`

**Request:**
```json
{
  "method": "MAX_SHARPE",
  "lookback": "3Y",
  "maxWeight": 0.35
}
```

*Supported Methods*: `MAX_SHARPE`, `MIN_VARIANCE`, `EQUAL_WEIGHT`.

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "method": "MAX_SHARPE",
    "expectedReturn": 0.1840,
    "expectedVolatility": 0.1120,
    "sharpeRatio": 1.6428,
    "weights": [
      { "symbol": "AAPL", "weight": 0.25 },
      { "symbol": "MSFT", "weight": 0.30 },
      { "symbol": "GLD", "weight": 0.20 },
      { "symbol": "BND", "weight": 0.25 }
    ],
    "calculatedAt": "2026-10-07T12:00:00Z"
  }
}
```

### 5.2 Stochastic Efficient Frontier
`POST /api/portfolios/{id}/efficient-frontier`

**Request:**
```json
{
  "simulations": 2000,
  "lookback": "3Y"
}
```

---

## 6. Monte Carlo Simulation

Simulates thousands of stochastic forward paths using geometric Brownian motion (GBM) with Cholesky asset return correlation.

`POST /api/portfolios/{id}/monte-carlo`

**Request:**
```json
{
  "simulations": 5000,
  "horizonDays": 252
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "simulations": 5000,
    "time_horizon_days": 252,
    "initial_portfolio_value": 100000.0,
    "mean_ending_value": 112450.20,
    "median_ending_value": 110820.00,
    "probability_of_loss": 0.1840,
    "percentiles": {
      "p5": 84200.00,
      "p25": 98500.00,
      "p50": 110820.00,
      "p75": 124300.00,
      "p95": 145600.00
    }
  }
}
```

---

## 7. Scenario Shocks & Historical Stress Testing

### 7.1 Custom Asset-Class / Symbol Shocks
`POST /api/portfolios/{id}/scenario`

**Request:**
```json
{
  "shocks": {
    "EQUITY": -0.25,
    "GOLD": 0.10,
    "BOND": 0.05
  }
}
```

### 7.2 Predefined Macro Historical Crises
`POST /api/portfolios/{id}/stress-tests`

Replays standard historical macro crisis shocks against current holdings:
1. `2008 Global Financial Crisis` (Equities -45%, Commodities -35%, Treasuries +15%)
2. `2020 COVID-19 Liquidity Shock` (Equities -30%, Gold +8%, Bonds +6%)
3. `Tech Stagflation Shock` (Tech Equities -35%, Commodities +20%, Bonds -12%)

**Request:**
```json
{
  "scenarios": [
    "2008 Global Financial Crisis",
    "2020 COVID-19 Liquidity Shock"
  ]
}
```
