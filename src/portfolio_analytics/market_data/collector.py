"""
Market Data Collector Orchestrator (Extract -> Validate -> Transform -> Load)
"""

from datetime import datetime
from typing import Dict, List, Optional
from sqlalchemy.orm import Session
from portfolio_analytics.market_data.base_provider import MarketDataProvider
from portfolio_analytics.market_data.validator import PriceValidator
from portfolio_analytics.market_data.transformer import PriceTransformer
from portfolio_analytics.database.repositories.price_repository import PriceRepository
from portfolio_analytics.database.models import Asset
from portfolio_analytics.config.logging_config import logger


class MarketDataCollector:
    """
    Coordinates data extraction from providers, runs validation and normalization,
    and persists cleaned records to the database.
    """

    def __init__(self, provider: MarketDataProvider, session: Session):
        self.provider = provider
        self.session = session
        self.price_repo = PriceRepository(session)

    def collect_historical_for_asset(
        self,
        asset: Asset,
        start_date: Optional[datetime] = None,
        end_date: Optional[datetime] = None,
        interval: str = "1day",
    ) -> Dict[str, int]:
        """
        Pulls historical prices for a single asset, validates, and stores them.
        """
        logger.info(f"Starting historical ingestion for {asset.symbol} ({asset.provider_symbol})")
        raw_records = self.provider.fetch_historical_prices(
            symbol=asset.provider_symbol,
            start_date=start_date,
            end_date=end_date,
            interval=interval,
        )

        if not raw_records:
            logger.warning(f"No records fetched for {asset.symbol}")
            return {"fetched": 0, "valid": 0, "inserted": 0}

        # Validate
        usable_records, reports = PriceValidator.validate_batch(raw_records)
        valid_count = len(usable_records)

        # Transform
        db_payloads = PriceTransformer.transform_batch(usable_records, asset.id)

        # Deduplicate and Load
        inserted_count = self.price_repo.insert_prices(db_payloads)
        self.session.commit()

        logger.info(
            f"Completed ingestion for {asset.symbol}: "
            f"Fetched={len(raw_records)}, Valid={valid_count}, Newly Inserted={inserted_count}"
        )
        return {
            "fetched": len(raw_records),
            "valid": valid_count,
            "inserted": inserted_count,
        }

    def collect_latest_for_asset(self, asset: Asset) -> bool:
        """
        Pulls the latest price candle for an asset, validates, and stores it.
        """
        latest_record = self.provider.fetch_latest_price(asset.provider_symbol)
        if not latest_record:
            return False

        res = PriceValidator.validate_record(latest_record)
        if not res.is_usable:
            logger.warning(f"Latest record for {asset.symbol} is invalid: {res.errors}")
            return False

        payload = PriceTransformer.transform_to_db_payload(latest_record, asset.id)
        self.price_repo.insert_prices([payload])
        self.session.commit()
        return True
