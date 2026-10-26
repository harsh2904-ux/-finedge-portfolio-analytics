"""
Market Data Transformer & Normalizer
"""

from datetime import datetime, timezone
from typing import Dict, List
from portfolio_analytics.market_data.base_provider import RawPriceRecord


class PriceTransformer:
    """Transforms raw records into database-ready dictionary structures."""

    @staticmethod
    def ensure_utc(dt: datetime) -> datetime:
        """Ensures datetime is timezone-aware UTC."""
        if dt.tzinfo is None:
            return dt.replace(tzinfo=timezone.utc)
        return dt.astimezone(timezone.utc)

    @classmethod
    def transform_to_db_payload(cls, record: RawPriceRecord, asset_id: int) -> dict:
        """Converts validated RawPriceRecord to MarketPrice table dictionary."""
        return {
            "asset_id": asset_id,
            "timestamp": cls.ensure_utc(record.timestamp),
            "open": round(float(record.open), 4),
            "high": round(float(record.high), 4),
            "low": round(float(record.low), 4),
            "close": round(float(record.close), 4),
            "adjusted_close": round(float(record.adjusted_close), 4) if record.adjusted_close is not None else None,
            "volume": float(record.volume),
            "source": record.source,
        }

    @classmethod
    def transform_batch(cls, records: List[RawPriceRecord], asset_id: int) -> List[dict]:
        return [cls.transform_to_db_payload(r, asset_id) for r in records]
