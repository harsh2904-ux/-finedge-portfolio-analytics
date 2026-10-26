"""
Twelve Data REST API Market Data Provider Implementation
"""

import time
from datetime import datetime, timezone
from typing import Dict, List, Optional
import requests
from portfolio_analytics.config.settings import settings
from portfolio_analytics.config.logging_config import logger
from portfolio_analytics.market_data.base_provider import (
    MarketDataProvider,
    RawPriceRecord,
    QuoteRecord,
)


class TwelveDataError(Exception):
    """Base exception for Twelve Data API issues."""
    pass


class RateLimitError(TwelveDataError):
    """Raised when API rate limits are exceeded."""
    pass


class InvalidSymbolError(TwelveDataError):
    """Raised when requested ticker does not exist."""
    pass


class TwelveDataProvider(MarketDataProvider):
    """
    Production-ready Twelve Data REST API Client.
    Features exponential backoff, rate limiting, and robust error handling.
    """

    def __init__(self, api_key: Optional[str] = None, base_url: Optional[str] = None):
        self.api_key = api_key or settings.TWELVE_DATA_API_KEY
        self.base_url = (base_url or settings.TWELVE_DATA_BASE_URL).rstrip("/")
        self.session = requests.Session()
        self.session.headers.update({
            "User-Agent": "MultiAssetPortfolioAnalytics/1.0",
            "Accept": "application/json",
        })

    def _execute_request(self, endpoint: str, params: dict, max_retries: int = 3) -> dict:
        """
        Executes HTTP GET request with exponential backoff and error classification.
        """
        params["apikey"] = self.api_key
        url = f"{self.base_url}/{endpoint}"

        backoff = 1.0
        for attempt in range(1, max_retries + 1):
            try:
                response = self.session.get(url, params=params, timeout=15)
                
                # Check for HTTP Rate Limiting
                if response.status_code == 429:
                    logger.warning(f"Twelve Data rate limit hit (attempt {attempt}/{max_retries}). Backing off for {backoff:.1f}s...")
                    time.sleep(backoff)
                    backoff *= 2
                    continue

                response.raise_for_status()
                data = response.json()

                # Check Twelve Data logical error responses
                if isinstance(data, dict) and data.get("status") == "error":
                    msg = data.get("message", "Unknown error")
                    code = data.get("code")
                    if code == 429 or "rate limit" in msg.lower():
                        logger.warning(f"Twelve Data API rate limit message: {msg}. Retrying in {backoff:.1f}s...")
                        time.sleep(backoff)
                        backoff *= 2
                        continue
                    elif code == 404 or "not found" in msg.lower() or "invalid symbol" in msg.lower():
                        raise InvalidSymbolError(f"Symbol {params.get('symbol')} invalid or not supported: {msg}")
                    elif code == 401 or "api key" in msg.lower():
                        raise TwelveDataError(f"Invalid Twelve Data API Key: {msg}")
                    else:
                        raise TwelveDataError(f"Twelve Data API Error ({code}): {msg}")

                return data

            except requests.exceptions.RequestException as e:
                logger.warning(f"Network error on Twelve Data request (attempt {attempt}/{max_retries}): {e}")
                if attempt == max_retries:
                    raise TwelveDataError(f"Failed to communicate with Twelve Data after {max_retries} attempts: {e}")
                time.sleep(backoff)
                backoff *= 2

        raise RateLimitError("Twelve Data API rate limit exceeded all retry attempts.")

    def fetch_historical_prices(
        self,
        symbol: str,
        start_date: Optional[datetime] = None,
        end_date: Optional[datetime] = None,
        interval: str = "1day",
    ) -> List[RawPriceRecord]:
        """
        Fetches historical OHLCV data from Twelve Data /time_series endpoint.
        """
        params = {
            "symbol": symbol,
            "interval": interval,
            "outputsize": 5000,
        }
        if start_date:
            params["start_date"] = start_date.strftime("%Y-%m-%d")
        if end_date:
            params["end_date"] = end_date.strftime("%Y-%m-%d")

        logger.info(f"Fetching historical prices from Twelve Data for symbol: {symbol}")
        data = self._execute_request("time_series", params)

        values = data.get("values", [])
        if not values:
            logger.warning(f"No historical price values returned for symbol: {symbol}")
            return []

        records: List[RawPriceRecord] = []
        for v in values:
            try:
                # Parse timestamp: Twelve Data format is YYYY-MM-DD or YYYY-MM-DD HH:MM:SS
                dt_str = v["datetime"]
                if " " in dt_str:
                    dt = datetime.strptime(dt_str, "%Y-%m-%d %H:%M:%S")
                else:
                    dt = datetime.strptime(dt_str, "%Y-%m-%d")
                dt = dt.replace(tzinfo=timezone.utc)

                record = RawPriceRecord(
                    symbol=symbol,
                    timestamp=dt,
                    open=float(v["open"]),
                    high=float(v["high"]),
                    low=float(v["low"]),
                    close=float(v["close"]),
                    volume=float(v.get("volume", 0.0) or 0.0),
                    source="twelve_data",
                )
                records.append(record)
            except (ValueError, KeyError) as e:
                logger.warning(f"Failed to parse candle data for {symbol}: {v} error: {e}")
                continue

        # Sort ascending by timestamp
        records.sort(key=lambda r: r.timestamp)
        logger.info(f"Retrieved {len(records)} price records for {symbol} from Twelve Data.")
        return records

    def fetch_latest_price(self, symbol: str) -> Optional[RawPriceRecord]:
        """
        Fetches the latest candle from Twelve Data.
        """
        history = self.fetch_historical_prices(symbol, interval="1day")
        if history:
            return history[-1]
        return None

    def fetch_quote(self, symbol: str) -> Optional[QuoteRecord]:
        """
        Fetches the latest quote metadata from Twelve Data /quote endpoint.
        """
        params = {"symbol": symbol}
        data = self._execute_request("quote", params)
        if not data:
            return None

        try:
            dt_str = data.get("datetime", "")
            if dt_str:
                dt = datetime.fromisoformat(dt_str) if "T" in dt_str else datetime.strptime(dt_str, "%Y-%m-%d")
            else:
                dt = datetime.now(timezone.utc)

            return QuoteRecord(
                symbol=symbol,
                price=float(data["close"]),
                timestamp=dt.replace(tzinfo=timezone.utc),
                open=float(data.get("open", 0.0) or 0.0),
                high=float(data.get("high", 0.0) or 0.0),
                low=float(data.get("low", 0.0) or 0.0),
                volume=float(data.get("volume", 0.0) or 0.0),
            )
        except Exception as e:
            logger.error(f"Error parsing quote for {symbol}: {e}")
            return None
