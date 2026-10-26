# Portfolio Risk & Analytics Backend Layer (Spring Boot)

Production-grade Spring Boot 3 enterprise REST API and business orchestration layer for the **Multi-Asset Investment Portfolio Risk & Performance Analytics** platform.

---

## 1. Features & Capabilities

- **Authentication & Security**:
  - Stateless JWT token authentication with HS256 algorithm.
  - BCrypt password hashing.
  - Tenant isolation: users can only view and modify their own portfolios.
  - Public endpoints: `/api/auth/register`, `/api/auth/login`, `/api/auth/refresh`, Swagger UI, Actuator health.
- **Portfolio & Asset Management**:
  - CRUD operations for Portfolios, Assets, Holdings, and Transactions.
  - Real-time mark-to-market valuation engine calculating total market value, daily PnL, unrealized PnL, and weighted cost basis.
  - Trade execution engine enforcing long-only trading constraints (rejects naked short selling).
- **Quantitative Analytics Orchestration**:
  - Delegates complex financial calculations to the Python/FastAPI analytics engine (`:8001`) via Spring's `RestClient`.
  - Caching layer for expensive analytics payloads.
  - Endpoints for Performance (CAGR, excess return), Risk (Sharpe, Sortino, VaR 95/99, Expected Shortfall, Drawdowns), Diversification (Asset allocation, correlation matrix, Euler risk contribution), Markowitz Optimization (Max Sharpe, Min Variance, Equal Weight), Efficient Frontier, Monte Carlo Simulation, and Macro Stress Testing.
- **Observability & Docs**:
  - Spring Actuator `/actuator/health` with custom health indicator checking Python analytics service status.
  - OpenAPI 3.0 / Swagger UI at `/swagger-ui/index.html`.

---

## 2. Technology Stack

- **Java**: 21+ / 25
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6 + JJWT 0.12.6 + BCrypt
- **Persistence**: Spring Data JPA + Hibernate 6 + HikariCP
- **Database**: PostgreSQL 16 (production/docker) / SQLite & H2 (testing/dev)
- **Database Migrations**: Flyway (versioned V1, V2, V3 scripts)
- **API Documentation**: Springdoc OpenAPI 2.6.0
- **Testing**: JUnit 5 + MockMvc + Spring Security Test

---

## 3. Quick Start

### Prerequisites
- JDK 21 or later
- Maven 3.9+
- Python 3.10+ (for analytics engine) or Docker / Docker Compose

### 1. Start the Python Analytics Engine
In the project root:
```bash
# Activate virtual environment
.venv\Scripts\activate   # Windows
# or source .venv/bin/activate # Linux/macOS

# Run FastAPI engine on port 8001
python scripts/run_fastapi.py
```

### 2. Build and Test the Spring Boot Application
```bash
cd backend

# Run all unit and integration tests
mvn test

# Package executable JAR
mvn package
```

### 3. Run the Spring Boot Application
```bash
# Run with default profile (PostgreSQL)
java -jar target/portfolio-analytics-backend-1.0.0.jar

# Or run with dev profile (SQLite local fallback)
java -Dspring.profiles.active=dev -jar target/portfolio-analytics-backend-1.0.0.jar
```

---

## 4. Running with Docker Compose

From the root project directory:
```bash
docker-compose up --build
```
This launches:
- `postgres`: PostgreSQL 16 on port `5432`
- `fastapi-analytics`: Python FastAPI analytics engine on port `8001`
- `spring-backend`: Spring Boot REST API on port `8080`

---

## 5. Testing with Postman

Import `Portfolio_Analytics_API.postman_collection.json` (located in the project root and `backend/`) into Postman.
1. Execute `1. Authentication -> Login User` or `Register New User`.
2. The login test script automatically stores the returned JWT token into the `{{jwtToken}}` collection variable.
3. Call any of the Asset, Market, Portfolio, Valuation, or Analytics endpoints.
