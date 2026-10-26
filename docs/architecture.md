# Architecture Design: Enterprise Multi-Asset Portfolio Risk & Analytics System

## 1. System Overview

The **Enterprise Multi-Asset Portfolio Risk & Performance Analytics Platform** is a distributed, high-performance financial engineering system built across two specialized tiers:
1. **Part 1 — Quantitative Financial Engine (Python / FastAPI / NumPy / Pandas)**:
   Responsible for heavy quantitative modeling, risk measurement, Cholesky-correlated Monte Carlo simulations, Markowitz mean-variance optimization, Euler component risk attribution, and macroeconomic stress testing.
2. **Part 2 — Enterprise Backend & REST API Layer (Java 21 / Spring Boot 3.3 / Spring Security / JPA / PostgreSQL)**:
   Responsible for user identity, JWT authentication, role-based access control, strict multi-tenant isolation, portfolio/holding/transaction lifecycle management, mark-to-market valuation, business validation (long-only execution), caching, and orchestrating upstream analytics calls via modern `RestClient`.

---

## 2. Distributed Architecture Diagram

```mermaid
flowchart TD
    subgraph Client Layer
        FE["React Single Page Application / Mobile Web"]
        PBI["Power BI / Executive Dashboards"]
        POSTMAN["Postman / API Clients"]
    end

    subgraph Security & Edge Layer
        CORS["CORS & Origin Filtering"]
        JWT_FILTER["JwtAuthenticationFilter"]
        AUTH_MGR["Spring Security AuthenticationManager"]
        SEC_CTX["SecurityContext (UserPrincipal)"]
    end

    subgraph Spring Boot Application Layer [Java 21 / Spring Boot 3.3]
        CTRL["REST Controllers: Auth, Asset, Market, Portfolio, Holding, Tx, Analytics"]
        VAL["Bean Validation: @Valid, JSR-380"]
        SRV["Business Services: PortfolioService, TransactionService, MarketDataService"]
        CACHE["Spring Cache Layer (ConcurrentHashMap / Redis-ready)"]
        CLIENT["PythonAnalyticsClient (RestClient with Timeout & Fallbacks)"]
        ERR["GlobalExceptionHandler (@RestControllerAdvice)"]
    end

    subgraph Persistence Layer
        FLYWAY["Flyway Database Migrations (V1, V2, V3)"]
        HIKARI["HikariCP Connection Pool"]
        JPA["Spring Data JPA / Hibernate ORM"]
        DB[("PostgreSQL 16 Database / SQLite Dev Fallback")]
    end

    subgraph Quantitative Engine [Python 3.12 / FastAPI]
        FASTAPI["FastAPI Analytics Server (:8001)"]
        ENG_RET["Returns & Benchmark Engine"]
        ENG_RISK["Risk Engine: VaR 95/99, Expected Shortfall, Drawdowns"]
        ENG_OPT["Markowitz Optimization & Efficient Frontier"]
        ENG_MC["Monte Carlo Simulation (Geometric Brownian Motion)"]
        ENG_STRESS["Macro Scenario Stress Engine (2008 GFC, COVID-19)"]
    end

    FE -->|HTTPS / REST + Bearer JWT| CORS
    PBI -->|HTTPS / REST + Bearer JWT| CORS
    POSTMAN -->|HTTPS / REST + Bearer JWT| CORS

    CORS --> JWT_FILTER
    JWT_FILTER --> AUTH_MGR
    AUTH_MGR --> SEC_CTX
    SEC_CTX --> CTRL

    CTRL --> VAL
    VAL --> SRV
    SRV --> CACHE
    SRV --> JPA
    SRV --> CLIENT

    JPA --> HIKARI
    HIKARI --> FLYWAY
    FLYWAY --> DB

    CLIENT -->|HTTP REST POST /analytics/*| FASTAPI
    FASTAPI --> ENG_RET
    FASTAPI --> ENG_RISK
    FASTAPI --> ENG_OPT
    FASTAPI --> ENG_MC
    FASTAPI --> ENG_STRESS
    FASTAPI -.->|Read Prices & Weights| DB
```

---

## 3. Separation of Responsibilities

### Java / Spring Boot Layer
- **Identity & Authorization**: BCrypt password hashing, signed JJWT tokens (access + refresh), user tenant isolation.
- **Data Integrity & Long-Only Validation**: Transaction recording enforces available share balance; prevents accidental naked short sales.
- **Valuation**: Fast in-memory mark-to-market calculations combining current holdings with latest market prices.
- **Caching**: Performance and risk computation payloads cached with automatic invalidation upon new trades or price updates.
- **API Standards**: Uniform JSON envelopes (`ApiResponse<T>`, `ApiErrorResponse`), standard HTTP status codes (200, 201, 400, 401, 403, 404, 409, 503).
- **Resilience**: Actuator custom health indicator monitors FastAPI connectivity. If the analytics engine is offline, requests return clean `503 Service Unavailable` with `ANALYTICS_SERVICE_UNAVAILABLE`.

### Python / FastAPI Quantitative Engine
- **Returns Analytics**: Arithmetic, logarithmic, cumulative, annualized CAGR, rolling excess returns, tracking error, information ratio.
- **Risk Analytics**: Volatility, downside semi-deviation, Sharpe ratio, Sortino ratio, CAPM Alpha and Beta.
- **Value at Risk (VaR)**: Historical percentile VaR (95% & 99%), Parametric Gaussian VaR, Conditional VaR (Expected Shortfall / CVaR), and Dollar VaR.
- **Diversification & Attribution**: Full covariance/correlation matrices, Euler risk decomposition (marginal and component percentage risk contributions).
- **Optimization**: Modern Portfolio Theory (MPT) mean-variance optimization (Max Sharpe, Min Variance, Equal Weight) and Efficient Frontier stochastic sampling.
- **Stochastic Simulation**: Correlated geometric Brownian motion paths over customizable horizons (e.g., 252 trading days, 5,000 paths).
- **Macro Stress Testing**: Instant historical scenario replay (2008 Financial Crisis, 2020 COVID-19 shock, Tech Stagflation).

---

## 4. Multi-Tenant Security & Isolation Model

Every portfolio is strictly linked via foreign key to a `User` entity (`user_id`).
- When any portfolio-scoped endpoint is called (`/api/portfolios/{id}/**`), the `PortfolioService.checkOwnership()` method verifies that the authenticated `UserPrincipal` matches the owner of the portfolio or holds `ROLE_ADMIN`.
- If an unauthenticated user attempts access: **401 Unauthorized** is returned.
- If an authenticated user attempts to access another user's portfolio: **403 Forbidden** is returned.
- Passwords are never serialized into DTOs or log outputs.
