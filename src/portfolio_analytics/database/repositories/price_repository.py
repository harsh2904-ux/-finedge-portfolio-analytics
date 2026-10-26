"""
Market Price Repository for storing and retrieving historical & latest OHLCV market data
"""

from datetime import datetime
from typing import Dict, List, Optional
import pandas as pd
from sqlalchemy import select, and_, func
from sqlalchemy.orm import Session
from portfolio_analytics.database.models import MarketPrice, Asset
from portfolio_analytics.config.logging_config import logger


class PriceRepository:
    """Repository handling MarketPrice persistence and time-series extraction."""

    def __init__(self, session: Session):
        self.session = session

    def insert_prices(self, price_records: List[dict]) -> int:
        """
        Inserts a list of raw price dicts, avoiding duplicates.
        Returns the number of successfully inserted rows.
        """
        if not price_records:
            return 0

        inserted_count = 0
        for record in price_records:
            asset_id = record["asset_id"]
            timestamp = record["timestamp"]
            source = record.get("source", "twelve_data")

            # Check if record already exists (avoiding unique constraint violation)
            stmt = select(MarketPrice.id).where(
                and_(
                    MarketPrice.asset_id == asset_id,
                    MarketPrice.timestamp == timestamp,
                    MarketPrice.source == source,
                )
            )
            existing_id = self.session.scalar(stmt)
            if existing_id is None:
                new_price = MarketPrice(
                    asset_id=asset_id,
                    timestamp=timestamp,
                    open=record["open"],
                    high=record["high"],
                    low=record["low"],
                    close=record["close"],
                    adjusted_close=record.get("adjusted_close"),
                    volume=record.get("volume", 0.0),
                    source=source,
                )
                self.session.add(new_price)
                inserted_count += 1

        self.session.flush()
        return inserted_count

    def get_latest_price(self, asset_id: int) -> Optional[MarketPrice]:
        """Gets the most recent market price for an asset."""
        stmt = (
            select(MarketPrice)
            .where(MarketPrice.asset_id == asset_id)
            .order_by(MarketPrice.timestamp.desc())
            .limit(1)
        )
        return self.session.scalar(stmt)

    def get_price_history(
        self,
        asset_id: int,
        start_date: Optional[datetime] = None,
        end_date: Optional[datetime] = None,
    ) -> List[MarketPrice]:
        """Fetches ordered historical prices for an asset."""
        conditions = [MarketPrice.asset_id == asset_id]
        if start_date:
            conditions.append(MarketPrice.timestamp >= start_date)
        if end_date:
            conditions.append(MarketPrice.timestamp <= end_date)

        stmt = select(MarketPrice).where(and_(*conditions)).order_by(MarketPrice.timestamp.asc())
        return list(self.session.scalars(stmt).all())

    def get_aligned_price_matrix(
        self,
        asset_ids: List[int],
        start_date: Optional[datetime] = None,
        end_date: Optional[datetime] = None,
        column_name: str = "close",
    ) -> pd.DataFrame:
        """
        Extracts historical close prices for multiple assets and pivots into
        an aligned DataFrame indexed by Date with asset symbols as columns.
        Handles date alignment and drops or forward fills missing dates.
        """
        if not asset_ids:
            return pd.DataFrame()

        conditions = [MarketPrice.asset_id.in_(asset_ids)]
        if start_date:
            conditions.append(MarketPrice.timestamp >= start_date)
        if end_date:
            conditions.append(MarketPrice.timestamp <= end_date)

        stmt = (
            select(
                MarketPrice.timestamp,
                Asset.symbol,
                getattr(MarketPrice, column_name).label("price"),
            )
            .join(Asset, MarketPrice.asset_id == Asset.id)
            .where(and_(*conditions))
            .order_by(MarketPrice.timestamp.asc())
        )
        rows = self.session.execute(stmt).all()
        if not rows:
            return pd.DataFrame()

        df = pd.DataFrame(rows, columns=["timestamp", "symbol", "price"])
        # Format date component
        df["date"] = pd.to_datetime(df["timestamp"]).dt.tz_localize(None).dt.normalize()
        
        # Deduplicate same date if multiple exist (take latest)
        df = df.drop_duplicates(subset=["date", "symbol"], keep="last")
        
        # Pivot into Date x Symbols
        matrix = df.pivot(index="date", columns="symbol", values="price")
        # Forward fill up to 3 days (e.g. weekend/holiday alignment), then drop remaining NaNs
        matrix = matrix.ffill(limit=3).dropna()
        return matrix
