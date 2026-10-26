# Data Pipeline: Ingestion, Validation & Scheduling

## 1. Overview

The Market Data Pipeline is responsible for extracting market data from Twelve Data (or any compatible provider), subjecting every candle to rigorous financial integrity validation, normalizing data into unified UTC schemas, deduplicating records, and persisting them to the database.

```text
Twelve Data REST API
        ↓
[1. Extract] MarketDataCollector / TwelveDataProvider
        ↓
[2. Validate] PriceValidator (OHLC consistency, positivity, non-negative volume)
        ↓
[3. Transform] PriceTransformer (Timestamp UTC normalization, internal symbol mapping)
        ↓
[4. Deduplicate] PriceRepository (Intra-batch & database unique constraint checks)
        ↓
[5. Load] PostgreSQL / SQLite Storage
```

---

## 2. Ingestion Stages

### Stage 1: Extraction (`MarketDataProvider` & `TwelveDataProvider`)
- **Abstract Contract**: `MarketDataProvider` defines:
  - `get_realtime_quote(symbol: str) -> Dict`
  - `get_historical_prices(symbol: str, interval: str, outputsize: int, start_date, end_date) -> List[Dict]`
- **Twelve Data Implementation**:
  - Connects to Twelve Data REST endpoint (`https://api.twelvedata.com/time_series` and `/quote`).
  - Implements **exponential backoff and retries** (up to 3 attempts with multiplier backoff: 1s, 2s, 4s).
  - Handles HTTP status codes 429 (Rate Limit Exceeded), 500, 502, 503, 504.
  - Automatically parses Twelve Data JSON structure (`{"values": [...], "status": "ok"}`). If the API key is not configured or exceeds quota, synthetic historical series can be seeded for continuous offline testing and verification.

### Stage 2: Data Validation (`PriceValidator`)
Every incoming price record is verified before transformation. Financial sanity constraints enforced:

```python
# Strict Positivity Checks
open > 0
high > 0
low > 0
close > 0

# OHLC Structural Relationships
high >= low
high >= open
high >= close
low <= open
low <= close

# Volume Bounds
volume >= 0
```

#### Validation Statuses:
- **`VALID`**: All rules satisfied. Price is accepted for transformation and storage.
- **`WARNING`**: Non-critical issue (e.g. high volume spike or minor tick discrepancy) flagged for audit without rejecting the bar.
- **`INVALID`**: Violation of physical trading invariants (e.g., negative price, High < Low, negative volume). Record is logged and rejected.

### Stage 3: Transformation (`PriceTransformer`)
- **Timestamp Standardization**: Parses varied formats (`YYYY-MM-DD`, `YYYY-MM-DD HH:MM:SS`, unix epochs) and converts to timezone-aware UTC ISO timestamps.
- **Symbol Alignment**: Translates provider symbols (e.g. `AAPL:NASDAQ` or `XAU/USD`) to internal asset records (`symbol="AAPL"`, `symbol="GLD"`).
- **Float Coercion**: Casts string values to standard 64-bit IEEE floating-point numbers.

### Stage 4: Deduplication & Loading (`PriceRepository`)
- Enforces uniqueness via the composite constraint: `uq_asset_time_source (asset_id, timestamp, source)`.
- Implements `insert_prices()` with existing record detection, preventing duplicate primary key exceptions and preserving idempotency on repeated pipeline runs.

---

## 3. Scheduled Data Ingestion (`APScheduler`)

Automated polling is orchestrated by `ScheduledIngestionPipeline`:

```python
from portfolio_analytics.pipelines.scheduled_pipeline import ScheduledIngestionPipeline

scheduler = ScheduledIngestionPipeline(interval_minutes=5)
scheduler.start()
```

### Scheduling Architecture:
1. **Configurable Interval**: Default is 5 minutes (`DATA_UPDATE_INTERVAL_MINUTES=5` in `.env`), fully customizable.
2. **Dynamic Asset Loading**: Reads all active instruments (`is_active = True`) from the `assets` table.
3. **Rate Limit Respect**: Batches queries to stay within Twelve Data API minute/daily credit allowances.
4. **Structured Logging**: Emits structured log records on cycle start, asset completion, record count, and errors.

---

## 4. Pipeline Execution CLI Commands

### 4.1. Initialize Asset Universe & DB
Populates the default multi-asset watchlist (`config/default_assets.json`):
```bash
python scripts/initialize_database.py
```

### 4.2. Ingest Historical Data
Fetches 1 year of daily historical prices for all watchlist assets:
```bash
python scripts/fetch_market_data.py --days 365 --interval 1day
```

### 4.3. Run End-to-End Orchestrator
Executes initialization, market data ingestion, sample portfolio creation, and quantitative analytics calculation in a single end-to-end command:
```bash
python scripts/run_pipeline.py
```
