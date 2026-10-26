# Quantitative Analytics Methodology

This document details the mathematical and statistical formulas implemented across the **Data & Analytics Engine**. All calculations are performed using `numpy`, `pandas`, and `scipy.optimize` with exact statistical formulas rather than approximations.

---

## 1. Return Calculations (`returns.py`)

### 1.1. Simple (Arithmetic) Return
For price series $P_t$:
$$R_t = \frac{P_t - P_{t-1}}{P_{t-1}} = \frac{P_t}{P_{t-1}} - 1$$

### 1.2. Logarithmic (Continuous) Return
$$r_t = \ln\left(\frac{P_t}{P_{t-1}}\right) = \ln(P_t) - \ln(P_{t-1})$$

### 1.3. Cumulative Return
Compounded over $T$ periods:
$$R_{\text{cum}, t} = \prod_{\tau=1}^t (1 + R_\tau) - 1$$

### 1.4. Compound Annual Growth Rate (CAGR)
Given initial portfolio value $V_0$, terminal value $V_T$, and duration in years $N = \frac{T}{252}$:
$$\text{CAGR} = \left(\frac{V_T}{V_0}\right)^{\frac{1}{N}} - 1$$

---

## 2. Risk Metrics (`risk.py`)

### 2.1. Annualized Volatility
Daily standard deviation $\sigma_{\text{daily}}$ annualized assuming 252 trading days:
$$\sigma_{\text{ann}} = \sigma_{\text{daily}} \times \sqrt{252}$$

### 2.2. Sharpe Ratio
Measures excess return per unit of total risk against the risk-free rate $R_f$:
$$\text{Sharpe} = \frac{R_p^{\text{ann}} - R_f}{\sigma_p^{\text{ann}}}$$

### 2.3. Downside Deviation & Sortino Ratio
Measures excess return penalizing only downside volatility below minimum acceptable return (MAR = $R_f$):
$$\sigma_{\text{down}} = \sqrt{\frac{1}{T} \sum_{t=1}^T \min(0, R_t - R_f)^2} \times \sqrt{252}$$

$$\text{Sortino} = \frac{R_p^{\text{ann}} - R_f}{\sigma_{\text{down}}}$$

### 2.4. Value-at-Risk (VaR)
VaR represents the maximum expected loss over a 1-day horizon at a given confidence level $1 - \alpha$ (e.g., 95%, 99%).

- **Parametric (Variance-Covariance) VaR**:
  Assuming normal distribution of daily returns with mean $\mu$ and standard deviation $\sigma$:
  $$\text{VaR}_{\alpha}^{\text{param}} = -(\mu - z_\alpha \sigma)$$
  where $z_{0.05} \approx 1.6449$ and $z_{0.01} \approx 2.3263$.

- **Historical VaR**:
  Empirical quantile of historical daily returns:
  $$\text{VaR}_{\alpha}^{\text{hist}} = -Q_\alpha(R)$$

### 2.5. Conditional Value-at-Risk (CVaR / Expected Shortfall)
The expected loss given that the loss exceeds the VaR threshold:
$$\text{CVaR}_\alpha = -\mathbb{E}[R \mid R \le - \text{VaR}_\alpha] = -\frac{1}{N_{\text{tail}}} \sum_{R_i \le - \text{VaR}_\alpha} R_i$$

---

## 3. Drawdown Analysis (`drawdown.py`)

For portfolio cumulative value series $V_t$:

1. **Running Peak**:
   $$M_t = \max_{0 \le \tau \le t} V_\tau$$

2. **Drawdown at time $t$**:
   $$\text{DD}_t = \frac{V_t - M_t}{M_t}$$

3. **Maximum Drawdown (MDD)**:
   $$\text{MDD} = \min_{t} \text{DD}_t$$

The engine tracks peak date, trough date, drawdown duration (days), and recovery date.

---

## 4. Benchmark & Market Sensitivity (`beta.py`, `correlation.py`)

### 4.1. Beta ($\beta$)
Measures sensitivity of portfolio returns $R_p$ to benchmark returns $R_m$:
$$\beta = \frac{\text{Cov}(R_p, R_m)}{\text{Var}(R_m)}$$

### 4.2. Jensen's Alpha ($\alpha$)
Measures risk-adjusted excess return over the Capital Asset Pricing Model (CAPM):
$$\alpha = R_p^{\text{ann}} - \left( R_f + \beta (R_m^{\text{ann}} - R_f) \right)$$

### 4.3. Tracking Error & Information Ratio
- **Tracking Error (TE)**:
  $$\text{TE} = \sigma(R_p - R_m) \times \sqrt{252}$$
- **Information Ratio (IR)**:
  $$\text{IR} = \frac{R_p^{\text{ann}} - R_m^{\text{ann}}}{\text{TE}}$$

---

## 5. Euler Risk Decomposition & Attribution (`attribution.py`)

Using Euler's theorem for homogeneous functions of degree 1, total portfolio volatility $\sigma_p = \sqrt{w^T \Sigma w}$ can be decomposed additively across $N$ assets:

1. **Marginal Contribution to Risk (MCR)**:
   $$\text{MCR}_i = \frac{\partial \sigma_p}{\partial w_i} = \frac{(\Sigma w)_i}{\sigma_p}$$

2. **Component Risk Contribution (CRC)**:
   $$\text{CRC}_i = w_i \times \text{MCR}_i = \frac{w_i (\Sigma w)_i}{\sigma_p}$$
   By Euler's theorem:
   $$\sum_{i=1}^N \text{CRC}_i = \sigma_p$$

3. **Percentage Contribution to Risk (PCR)**:
   $$\text{PCR}_i = \frac{\text{CRC}_i}{\sigma_p} = \frac{w_i (\Sigma w)_i}{\sigma_p^2}$$
   It is mathematically guaranteed that:
   $$\sum_{i=1}^N \text{PCR}_i = 1.0 \quad (100\%)$$

---

## 6. Portfolio Optimization (`optimization/`)

Given expected return vector $\mu$ and covariance matrix $\Sigma$:

### 6.1. Minimum Variance Portfolio (`minimum_variance.py`)
Find weights $w$ that minimize overall portfolio risk subject to long-only budget constraints:
$$\min_w \quad w^T \Sigma w$$
$$\text{subject to} \quad \sum_{i=1}^N w_i = 1, \quad 0 \le w_i \le 1$$

Solved via Sequential Least Squares Programming (SLSQP).

### 6.2. Maximum Sharpe Ratio Portfolio (`maximum_sharpe.py`)
Maximize risk-adjusted return over risk-free rate:
$$\max_w \quad \frac{w^T \mu - R_f}{\sqrt{w^T \Sigma w}}$$
$$\text{subject to} \quad \sum_{i=1}^N w_i = 1, \quad 0 \le w_i \le 1$$

Solved by minimizing the negative Sharpe ratio via SLSQP.

### 6.3. Efficient Frontier Engine (`efficient_frontier.py`)
Generates 5,000 randomized long-only Dirichlet/uniform weight allocations across the asset space, computes their annualized return and volatility coordinates, and identifies the upper boundary curve (Markowitz Efficient Frontier).

---

## 7. Monte Carlo Simulation (`monte_carlo.py`)

Simulates $M = 10,000$ correlated multivariate price trajectories over a 1-year horizon ($T = 252$ trading days) using Geometric Brownian Motion (GBM):

1. **Cholesky Factorization**:
   Decompose daily covariance matrix $\Sigma = L L^T$ where $L$ is the lower triangular Cholesky factor.
2. **Correlated Normal Innovations**:
   $$Z \sim \mathcal{N}(0, I) \implies \epsilon = L Z \sim \mathcal{N}(0, \Sigma)$$
3. **Price Progression**:
   For each asset $i$:
   $$S_{i, t+1} = S_{i, t} \exp\left( \left(\mu_i - \frac{1}{2} \sigma_i^2\right) \Delta t + \epsilon_{i, t} \sqrt{\Delta t} \right)$$
4. **Portfolio Value Paths**:
   For each path $k \in \{1, \dots, M\}$:
   $$V_t^{(k)} = \sum_{i=1}^N q_i S_{i, t}^{(k)}$$
5. **Terminal Distribution Quantiles**:
   Computes terminal 5th, 25th, 50th (median), 75th, and 95th percentiles of portfolio value.

---

## 8. Scenario & Stress Testing (`scenarios/`)

Simulates instantaneous macroeconomic shocks across asset classes based on historical stress regimes:

| Scenario | Equity Shock | Commodity Shock | Bond Shock | REIT Shock |
| :--- | :--- | :--- | :--- | :--- |
| **2008 Global Financial Crisis** | -45.0% | -30.0% | +12.0% | -50.0% |
| **2020 COVID-19 Liquidity Shock** | -30.0% | -25.0% | +6.0% | -35.0% |
| **2022 Tech Stagflation** | -25.0% | +20.0% | -15.0% | -20.0% |
| **Tech & Risk-On Meltup** | +30.0% | +10.0% | -4.0% | +15.0% |

### Impact Calculation:
For each holding $i$ with current market value $V_i = q_i \cdot P_i$ and asset class $C_i$:
$$V_i^{\text{stressed}} = V_i \times (1 + \Delta_{C_i})$$
$$\Delta V_{\text{portfolio}} = \sum_{i=1}^N V_i^{\text{stressed}} - \sum_{i=1}^N V_i$$
$$\text{Portfolio Impact (\%)} = \frac{\Delta V_{\text{portfolio}}}{\sum_{i=1}^N V_i} \times 100$$
