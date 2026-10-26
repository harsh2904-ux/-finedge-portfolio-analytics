# REST API Specification & Overview

## 1. Base URL & Protocol

- **Base URL**: `http://localhost:8080/api` (Production: HTTPS)
- **Content Type**: `application/json; charset=UTF-8`
- **Authentication**: Bearer Token in `Authorization: Bearer <JWT_TOKEN>` header
- **Interactive Documentation**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI 3.0 JSON**: `http://localhost:8080/v3/api-docs`

---

## 2. Standard Response Envelope

All API endpoints return predictable, standardized JSON envelopes.

### Success Response Envelope (`ApiResponse<T>`)
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully.",
  "timestamp": "2026-10-07T12:00:00Z"
}
```

### Paginated Response Envelope (`PagedResponse<T>`)
Used for large collections such as transactions:
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "last": false
}
```

### Error Response Envelope (`ApiErrorResponse`)
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed for provided fields.",
    "fieldErrors": {
      "quantity": "Quantity must be greater than zero",
      "price": "Price must be positive"
    }
  },
  "timestamp": "2026-10-07T12:00:00Z"
}
```

---

## 3. Standard HTTP Status Codes

| Code | Status | Meaning / Use Case |
| :--- | :--- | :--- |
| `200` | OK | Successful retrieval or idempotent update. |
| `201` | Created | Successfully created a new resource (portfolio, transaction, user). |
| `400` | Bad Request | Validation failure, illegal input arguments, or business rule violation (e.g. attempting to sell more shares than owned). |
| `401` | Unauthorized | Missing, expired, or malformed JWT token or invalid credentials. |
| `403` | Forbidden | Authenticated user is attempting to access a portfolio belonging to another user. |
| `404` | Not Found | Requested entity (Asset, Portfolio, Holding, Transaction) does not exist. |
| `409` | Conflict | Duplicate resource conflict (e.g., email already registered, asset symbol already exists). |
| `503` | Service Unavailable | Upstream quantitative Python analytics service is down or unreachable. |
| `500` | Internal Server Error | Unexpected server error with secure message (no stack traces exposed to client). |

---

## 4. Endpoints Summary Table

| Category | Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/auth/register` | Register new user account | No |
| **Auth** | `POST` | `/api/auth/login` | Authenticate credentials and receive JWT | No |
| **Auth** | `POST` | `/api/auth/refresh` | Refresh access token using refresh token | No |
| **Auth** | `GET` | `/api/auth/me` | Fetch authenticated user profile | Yes |
| **Assets** | `GET` | `/api/assets` | Filter and list all available assets | Yes |
| **Assets** | `GET` | `/api/assets/{id}` | Get single asset details | Yes |
| **Assets** | `POST` | `/api/assets` | Create a new asset (Admin/User) | Yes |
| **Assets** | `PUT` | `/api/assets/{id}` | Update existing asset | Yes |
| **Assets** | `DELETE` | `/api/assets/{id}` | Deactivate asset | Yes |
| **Market** | `GET` | `/api/market/{symbol}/latest` | Fetch latest price & daily change | Yes |
| **Market** | `GET` | `/api/market/{symbol}/history` | Historical OHLCV market bars | Yes |
| **Portfolios** | `GET` | `/api/portfolios` | List portfolios belonging to current user | Yes |
| **Portfolios** | `POST` | `/api/portfolios` | Create a new portfolio | Yes |
| **Portfolios** | `GET` | `/api/portfolios/{id}` | Get portfolio by ID | Yes (Owner) |
| **Portfolios** | `PUT` | `/api/portfolios/{id}` | Update portfolio settings | Yes (Owner) |
| **Portfolios** | `DELETE`| `/api/portfolios/{id}` | Delete portfolio | Yes (Owner) |
| **Portfolios** | `GET` | `/api/portfolios/{id}/valuation` | Real-time mark-to-market portfolio valuation | Yes (Owner) |
| **Holdings** | `GET` | `/api/portfolios/{id}/holdings` | List current holdings | Yes (Owner) |
| **Holdings** | `POST` | `/api/portfolios/{id}/holdings` | Add holding to portfolio | Yes (Owner) |
| **Holdings** | `PUT` | `/api/portfolios/{id}/holdings/{hId}` | Update holding quantity/target weight | Yes (Owner) |
| **Holdings** | `DELETE`| `/api/portfolios/{id}/holdings/{hId}` | Remove holding | Yes (Owner) |
| **Transactions**| `GET` | `/api/portfolios/{id}/transactions` | Paginated trade transaction history | Yes (Owner) |
| **Transactions**| `POST`| `/api/portfolios/{id}/transactions` | Record BUY / SELL transaction | Yes (Owner) |
| **Analytics** | `GET` | `/api/portfolios/{id}/performance` | CAGR, annualized return, benchmark comparison | Yes (Owner) |
| **Analytics** | `GET` | `/api/portfolios/{id}/risk` | Volatility, Sharpe, Sortino, Alpha, Beta | Yes (Owner) |
| **Analytics** | `GET` | `/api/portfolios/{id}/risk/var` | Historical & Parametric VaR, Expected Shortfall | Yes (Owner) |
| **Analytics** | `GET` | `/api/portfolios/{id}/risk/drawdown` | Maximum drawdown & recovery analysis | Yes (Owner) |
| **Analytics** | `GET` | `/api/portfolios/{id}/allocation` | Asset class weights and breakdown | Yes (Owner) |
| **Analytics** | `GET` | `/api/portfolios/{id}/correlation` | Correlation and covariance matrix | Yes (Owner) |
| **Analytics** | `GET` | `/api/portfolios/{id}/risk-contribution`| Marginal & component risk attribution | Yes (Owner) |
| **Analytics** | `POST`| `/api/portfolios/{id}/optimization` | Mean-variance Markowitz optimization | Yes (Owner) |
| **Analytics** | `POST`| `/api/portfolios/{id}/efficient-frontier`| Efficient frontier simulation points | Yes (Owner) |
| **Analytics** | `POST`| `/api/portfolios/{id}/monte-carlo` | Correlated stochastic forward simulation | Yes (Owner) |
| **Analytics** | `POST`| `/api/portfolios/{id}/scenario` | Custom asset-class / symbol shock replay | Yes (Owner) |
| **Analytics** | `POST`| `/api/portfolios/{id}/stress-tests` | Predefined macro crises (2008 GFC, COVID-19) | Yes (Owner) |
| **System** | `GET` | `/actuator/health` | Service health (includes Python engine status) | No |
