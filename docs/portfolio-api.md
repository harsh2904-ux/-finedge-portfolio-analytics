# Portfolio, Holdings & Transaction Management API Guide

## 1. Portfolio Management

Every portfolio belongs to an authenticated user (`user_id`). Users can maintain multiple portfolios (e.g., "Long-Term Growth", "Tech Growth", "Retirement All-Weather").

### 1.1 List Portfolios
`GET /api/portfolios`  
Returns all portfolios owned by the authenticated caller.

### 1.2 Create Portfolio
`POST /api/portfolios`

```json
{
  "name": "Global Multi-Asset Growth",
  "baseCurrency": "USD",
  "benchmarkSymbol": "SPY"
}
```

### 1.3 Portfolio Mark-to-Market Valuation
`GET /api/portfolios/{id}/valuation`

Computes the real-time mark-to-market valuation across all portfolio holdings using the latest market close price.

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "portfolioId": 1,
    "portfolioName": "Global Multi-Asset Growth",
    "portfolioValue": 125430.50,
    "dailyPnL": 1420.30,
    "dailyReturn": 0.0114,
    "totalCostBasis": 110200.00,
    "totalUnrealizedPnL": 15230.50,
    "totalUnrealizedPnLPercent": 0.1382,
    "currency": "USD",
    "asOf": "2026-10-07T15:30:00Z",
    "positions": [
      {
        "assetId": 1,
        "symbol": "AAPL",
        "name": "Apple Inc.",
        "assetClass": "EQUITY",
        "quantity": 100.0,
        "averageCost": 210.50,
        "currentPrice": 245.30,
        "marketValue": 24530.00,
        "weight": 0.1956,
        "costBasis": 21050.00,
        "unrealizedPnL": 3480.00,
        "unrealizedPnLPercent": 0.1653,
        "dailyReturn": 0.0085
      }
    ]
  }
}
```

---

## 2. Holdings API

Holdings represent snapshot positions within a portfolio.

### 2.1 Add Holding
`POST /api/portfolios/{portfolioId}/holdings`

```json
{
  "symbol": "NVDA",
  "quantity": 25.0,
  "averageCost": 125.40,
  "targetWeight": 0.20
}
```

**Validation Rules:**
- `quantity > 0`
- `averageCost > 0`
- `0.0 <= targetWeight <= 1.0`

### 2.2 Update Holding
`PUT /api/portfolios/{portfolioId}/holdings/{holdingId}`

Allows updating quantity, average cost, or strategic target allocation weight.

---

## 3. Transaction Execution Engine

The transaction engine records historical and live order executions (`BUY`, `SELL`, `DIVIDEND`, `FEE`) and automatically maintains holding position quantities and weighted average costs.

### 3.1 Long-Only Policy Enforcement
- When recording a `SELL` transaction, the engine checks the existing position quantity.
- If `quantityToSell > currentQuantity`, the transaction is rejected immediately with a `400 Bad Request`:
  ```json
  {
    "success": false,
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "Cannot sell 50 shares of AAPL: portfolio only holds 20 shares (short selling not permitted)."
    }
  }
  ```

### 3.2 Weighted Average Cost Calculation
On each `BUY` transaction:
$$\text{New Average Cost} = \frac{(\text{Current Qty} \times \text{Current Cost}) + (\text{Buy Qty} \times \text{Buy Price})}{\text{Current Qty} + \text{Buy Qty}}$$

### 3.3 Record Transaction
`POST /api/portfolios/{portfolioId}/transactions`

**Request:**
```json
{
  "symbol": "AAPL",
  "transactionType": "BUY",
  "quantity": 10.0,
  "price": 225.50,
  "transactionCost": 1.50,
  "currency": "USD"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 14,
    "portfolioId": 1,
    "assetId": 1,
    "symbol": "AAPL",
    "assetName": "Apple Inc.",
    "transactionType": "BUY",
    "quantity": 10.0,
    "price": 225.50,
    "totalAmount": 2256.50,
    "transactionCost": 1.50,
    "currency": "USD",
    "transactionDate": "2026-10-07T12:00:00Z"
  }
}
```
