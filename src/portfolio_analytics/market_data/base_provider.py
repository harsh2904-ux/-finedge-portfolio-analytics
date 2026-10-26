"""
Base Market Data Provider Interface and Data Transfer Objects
"""

from abc import ABC, abstractmethod
from datetime import datetime
from typing import Dict, List, Optional
from pydantic import BaseModel, Field


class RawPriceRecord(BaseModel):
    """Normalized raw price record from any market data vendor."""
    symbol: str
    timestamp: datetime
    open: float
    high: float
    low: float
    close: float
    adjusted_close: Optional[float] = None
    volume: float = 0.0
    source: str = "market_provider"


class QuoteRecord(BaseModel):
    """Latest quote metadata for a symbol."""
    symbol: str
    price: float
    timestamp: datetime
    open: Optional[float] = None
    high: Optional[float] = None
    low: Optional[float] = None
    volume: Optional[float] = None


class MarketDataProvider(ABC):
    """
    Abstract interface for external market data providers.
    Ensures provider-agnostic data collection.
    """

    @abstractmethod
    def fetch_historical_prices(
        self,
        symbol: str,
        start_date: Optional[datetime] = None,
        end_date: Optional[datetime] = None,
        interval: str = "1day",
    ) -> List[RawPriceRecord]:
        """Fetch historical OHLCV candles."""
        pass

    @abstractmethod
    def fetch_latest_price(self, symbol: str) -> Optional[RawPriceRecord]:
        """Fetch the most recent available price candle."""
        pass

    @abstractmethod
    def fetch_quote(self, symbol: str) -> Optional[QuoteRecord]:
        """Fetch real-time / delayed quote."""
        pass
