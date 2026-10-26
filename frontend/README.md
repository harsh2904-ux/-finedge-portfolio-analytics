# Part 3 — React Frontend & Business Intelligence Terminal

**Multi-Asset Investment Portfolio Risk & Performance Analytics**

An advanced dark financial analytics web terminal built with **React 19**, **TypeScript**, **Tailwind CSS**, and **Apache ECharts**. Consumes the Spring Boot REST API layer (Part 2) backed by the Python Quantitative Analytics Engine (Part 1).

---

## Architecture Overview

```text
                    ┌───────────────────────────────┐
                    │        React Frontend         │
                    │   React 19 + TypeScript       │
                    │   Tailwind + Apache ECharts   │
                    └───────────────┬───────────────┘
                                    │
                               REST │ (JWT Auth)
                                    ▼
                    ┌───────────────────────────────┐
                    │      Spring Boot Backend      │
                    │          (Port 8080)          │
                    └───────────────┬───────────────┘
                                    │
                   ┌────────────────┴────────────────┐
                   ▼                                 ▼
         ┌───────────────────┐             ┌───────────────────┐
         │ PostgreSQL DB     │             │  Python FastAPI   │
         │ Market & Holding  │             │ Risk, MPT, MC     │
         │ Data              │             │ (Port 8001)       │
         └───────────────────┘             └───────────────────┘
```

- **Strict Separation of Concerns**: React does zero financial math duplication; all quantitative outputs (Markowitz efficient frontier, Monte Carlo Brownian motion cones, Value at Risk, stress test drawdowns) originate from Spring Boot and the Python analytics engine.
- **Dark & Light Themes**: Tailored terminal palette (`#0B0E14` background, `#121824` cards, `#202B3E` borders, `#3B82F6` primary blue, `#10B981` profit emerald, `#EF4444` risk red) alongside a clean light mode.

---

## Features & Pages

| Module | Route | Functionality |
| :--- | :--- | :--- |
| **Authentication** | `/login`, `/register` | Secure JWT authentication with 1-click Demo credentials |
| **Executive Dashboard** | `/dashboard` | KPIs (AUM, P&L, Sharpe, 1-Day VaR), Performance chart vs SPY, Allocation donut, live holdings |
| **Portfolios** | `/portfolios` | Portfolio strategy cards, creation modal, delete confirmation, active selection |
| **Portfolio Detail** | `/portfolios/:id` | Tabbed position ledger, Add Holding modal, Record Trade execution (BUY/SELL) |
| **Market Data** | `/markets` | Multi-asset instrument search, asset-class filtering, interactive 1Y OHLC Candlestick modal |
| **Performance** | `/performance` | Cumulative return vs SPY (1M/3M/6M/1Y/3Y/5Y), Jensen's Alpha, Beta, Tracking Error |
| **Risk Analytics** | `/risk` | Parametric, Historical & Monte Carlo VaR (95%/99%), CVaR, Interactive Drawdown underwater curve |
| **Diversification** | `/diversification` | N×N correlation heatmap, marginal risk contribution vs capital allocation bar chart, HHI score |
| **Portfolio Optimization** | `/optimization` | Markowitz Modern Portfolio Theory (MPT), Efficient Frontier interactive curve, optimal rebalance deltas |
| **Monte Carlo Simulation** | `/simulation` | Geometric Brownian Motion fan chart (5th to 95th percentiles), terminal loss probability |
| **Stress Testing** | `/scenarios` | Historical crisis replay (2008 GFC, 2020 COVID, 2022 Fed shock) & custom macro factor shock builder |
| **BI Factsheet Report** | `/reports` | Print/PDF ready executive monthly factsheet and summary deck |
| **Settings & Diagnostics** | `/settings` | User profile, preferences, live ping diagnostics for Spring Boot (8080) and FastAPI (8001) |

---

## Tech Stack

- **Core Framework**: React 19, TypeScript, Vite
- **Styling**: Tailwind CSS, PostCSS, Lucide React icons, Framer Motion
- **Visualizations**: Apache ECharts (`echarts`, `echarts-for-react`)
- **State & Data Fetching**: Zustand (stores for auth, active portfolio, UI state), TanStack Query (cache, background refetching)
- **Forms & Validation**: React Hook Form, Zod
- **HTTP Client**: Axios with JWT request interceptor and automatic token refresh
- **Testing**: Vitest, React Testing Library, jsdom
- **Containerization**: Multi-stage Dockerfile with Nginx SPA reverse proxy

---

## Getting Started

### Prerequisites

- Node.js 20+
- npm 10+
- Spring Boot Backend running on `http://localhost:8080` (or Docker)

### Installation

```bash
cd frontend
npm install
```

### Development Server

```bash
npm run dev
```

The terminal will be accessible at: `http://localhost:3000`
Vite automatically proxies `/api` and `/actuator` requests to `http://localhost:8080`.

### Running Tests

```bash
npm run test
```

### Production Build

```bash
npm run build
```

The compiled SPA bundle will be placed in `frontend/dist/`.

### Docker Deployment

```bash
docker build -t portfolio-analytics-frontend .
docker run -p 80:80 portfolio-analytics-frontend
```
