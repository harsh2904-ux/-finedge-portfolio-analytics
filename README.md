# FinEdge — Indian Portfolio Analytics Terminal

[![Frontend](https://img.shields.io/badge/Frontend-React%2019%20%7C%20TypeScript%20%7C%20Tailwind-blue.svg)](https://react.dev/)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203%20%7C%20Java%2021-orange.svg)](https://spring.io/projects/spring-boot)
[![Analytics](https://img.shields.io/badge/Quant%20Engine-Python%20FastAPI%20%7C%20SciPy-green.svg)](https://fastapi.tiangolo.com/)
[![Tests](https://img.shields.io/badge/Unit%20Tests-42%20Passed-brightgreen.svg)]()
[![Market](https://img.shields.io/badge/Market-NSE%20India%20%7C%20INR%20%E2%82%B9-red.svg)](https://www.nseindia.com/)

An institutional-grade, full-stack investment portfolio analytics and quantitative risk management platform built for the **Indian Stock Market (NSE)**. 

FinEdge combines an enterprise **Java Spring Boot** transactional core, a high-performance **Python FastAPI** quantitative computing engine, and a Bloomberg/Zerodha-inspired **React 19** financial terminal with instant **Light Mode (☀️) & Dark Mode (🌙)** support.

---

## 1. System Architecture

```text
┌────────────────────────────────────────────────────────────────────────┐
│  TIER 1: WEB TERMINAL FRONTEND (React 19 + TypeScript + Tailwind CSS) │
│  • Institutional FinTech Terminal with Light ☀️ / Dark 🌙 Themes      │
│  • Interactive ECharts (Candlesticks, Efficient Frontier, Donut, Fan)  │
│  • Zustand Global State + TanStack Query Automatic Cache & Invalidation│
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP REST / JWT Bearer Authorization
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│  TIER 2: ENTERPRISE REST API (Spring Boot 3 + Java 21)                 │
│  • Stateless Spring Security & JWT Session Management                  │
│  • Transactional Ledger (Holdings, Buy/Sell Trades, Average Cost)      │
│  • Spring Data JPA with SQLite (Dev) / PostgreSQL (Prod) Persistence   │
│  • Spring Boot Actuator System Health Telemetry & Metrics              │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Microservice Contract (Port 8001)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│  TIER 3: QUANTITATIVE ANALYTICS ENGINE (Python 3.12+ FastAPI + SciPy)  │
│  • Markowitz Efficient Frontier (SLSQP Quadratic Programming)          │
│  • Monte Carlo Engine (10,000 Paths via Cholesky Decomposition)        │
│  • Euler Risk Decomposition (Marginal & Component Risk Contribution)   │
│  • Parametric & Historical Value-at-Risk (VaR) & Expected Shortfall    │
│  • Macroeconomic Stress Testing (GFC 2008, COVID 2020 Liquidity Shock) │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Key Capabilities & Feature Modules

### 📈 Indian Equities & Portfolio Management
- **NSE Universe Support**: Tracks top Indian equities (**RELIANCE, TCS, HDFCBANK, INFY, ICICIBANK, BHARTIARTL, ITC, LT, SBIN, TATAMOTORS**) and the **NIFTYBEES** benchmark ETF.
- **Transactional Ledger**: Automatically recalculates volume-weighted average cost basis and real-time Unrealized P&L in Indian Rupees (`₹`) upon recording buy or sell orders.
- **Live Market Watch**: Real-time price tracking with interactive daily OHLC candlestick charts and volume bars.

### 🛡️ Institutional Risk & Safety Limits
- **Value-at-Risk (VaR)**: 95% and 99% parametric and historical maximum loss forecasts.
- **Conditional VaR (Expected Shortfall)**: Quantifies the average loss magnitude in the worst 5% tail risk events.
- **Maximum Drawdown (MDD)**: Historical peak-to-trough drop tracking and recovery duration analysis.
- **Euler Risk Decomposition**: Proves the mathematical risk contribution of each constituent asset where $\sum \text{Risk Contribution}_i = 100\%$.

### 🎯 Portfolio Optimization (Markowitz Modern Portfolio Theory)
- Uses **Sequential Least Squares Programming (SLSQP)** to generate:
  - **Minimum Variance Portfolio**: Lowest possible risk combination.
  - **Maximum Sharpe Portfolio**: Highest expected return per unit of volatility.
  - **Efficient Frontier Envelope**: Interactive scatter curve comparing your current asset mix against the mathematical frontier.

### 🎲 Future Money Predictor (Stochastic Monte Carlo)
- Simulates **10,000 correlated price paths** forward across 252 trading days.
- Retains cross-asset variance using **Cholesky factor decomposition** ($L L^T = \Sigma$).
- Plots 5th, 25th, 50th (Median), 75th, and 95th percentile outcome fans in Indian Rupees (`₹`).

### 💥 Macroeconomic Stress Testing (Crash Testing)
- Simulates how your portfolio would perform during major financial shocks:
  - **2008 Global Financial Crisis** (severe equity sell-off, flight to gold)
  - **2020 COVID Liquidity Shock** (rapid market-wide crash)
  - **Tech Stagflation & Multiple Contraction**

### 🎨 Institutional User Experience
- **Light Mode (☀️) & Dark Mode (🌙)** with seamless header toggle switch and local persistence.
- High-contrast typography: Dark slate (`#0F172A`) on white cards in Light Mode; Crisp slate (`#F1F5F9`) on dark panels in Dark Mode.
- Responsive layout with collapsible sidebar and mobile drawer navigation.

---

## 3. Quick Start Guide

### Option A: 1-Click Launch (Windows)
Double-click **`start.bat`** in the project root. It will:
1. Launch the Python FastAPI Quantitative Engine on port `8001`.
2. Launch the Spring Boot Enterprise Backend on port `8080`.
3. Launch the Vite React Web Terminal on port `3000`.
4. Automatically open your browser to `http://localhost:3000`.

To stop all services, run **`stop.bat`**.

---

### Option B: Manual Multi-Terminal Startup

#### 1. Python Analytics Engine (Port 8001)
```bash
# Activate virtual environment and start FastAPI
.venv\Scripts\python.exe scripts/run_fastapi.py
```

#### 2. Spring Boot Backend (Port 8080)
```bash
# Run pre-compiled Spring Boot JAR
java -Dspring.profiles.active=dev -jar backend/target/portfolio-analytics-backend-1.0.0.jar
```

#### 3. React Frontend (Port 3000)
```bash
cd frontend
npm install
npm run dev
```

---

## 4. Default Login Credentials

| Attribute | Value |
|---|---|
| **URL** | [http://localhost:3000](http://localhost:3000) |
| **Email** | `demo@portfolio.com` |
| **Password** | `password123` |

---

## 5. API Endpoints & Documentation

- **Spring Boot Swagger OpenAPI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Spring Boot Actuator Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **FastAPI OpenAPI Swagger**: [http://localhost:8001/docs](http://localhost:8001/docs)
- **FastAPI Health**: [http://localhost:8001/health](http://localhost:8001/health)

---

## 6. Automated Test Suite

All 42 unit tests pass across both tiers:

```bash
# Run Frontend Tests (15 passed)
npm --prefix frontend test

# Run Python Quantitative Tests (27 passed)
.venv\Scripts\python.exe -m pytest tests/unit
```

---

## 7. Technology Stack Summary

- **Frontend**: React 19, TypeScript, Tailwind CSS, TanStack Query v5, Zustand, ECharts (echarts-for-react), Lucide Icons, Vite 8.
- **Backend**: Spring Boot 3.3, Java 21, Spring Security (Stateless JWT), Spring Data JPA, Hibernate, SQLite / PostgreSQL, Spring Boot Actuator.
- **Quantitative Engine**: Python 3.12+, FastAPI, Uvicorn, NumPy, SciPy (SLSQP Optimize), Pandas, Pydantic v2.
