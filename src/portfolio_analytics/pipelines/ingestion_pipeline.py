"""
Market Data Ingestion Pipeline
"""

from datetime import datetime
from typing import Dict, List, Optional
from sqlalchemy.orm import Session
from portfolio_analytics.database.repositories.asset_repository import AssetRepository
from portfolio_analytics.market_data.collector import MarketDataCollector
from portfolio_analytics.market_data.base_provider import MarketDataProvider
from portfolio_analytics.config.logging_config import logger


class IngestionPipeline:
    """
    Coordinates asset watchlist ingestion and price collection.
    """

    def __init__(self, provider: MarketDataProvider, session: Session):
        self.provider = provider
        self.session = session
        self.asset_repo = AssetRepository(session)
        self.collector = MarketDataCollector(provider, session)

    def run_historical_ingestion(
        self,
        symbols: Optional[List[str]] = None,
        start_date: Optional[datetime] = None,
        end_date: Optional[datetime] = None,
        interval: str = "1day",
    ) -> Dict[str, dict]:
        """
        Runs historical ingestion for all active assets or a specific subset of symbols.
        """
        logger.info("Starting historical market data ingestion pipeline...")
        all_assets = self.asset_repo.get_all_active()

        if symbols:
            target_symbols = set(s.upper() for s in symbols)
            assets_to_ingest = [a for a in all_assets if a.symbol in target_symbols]
        else:
            assets_to_ingest = all_assets

        results = {}
        for asset in assets_to_ingest:
            try:
                res = self.collector.collect_historical_for_asset(
                    asset=asset,
                    start_date=start_date,
                    end_date=end_date,
                    interval=interval,
                )
                results[asset.symbol] = res
            except Exception as e:
                logger.error(f"Failed to ingest historical data for {asset.symbol}: {e}")
                results[asset.symbol] = {"error": str(e), "fetched": 0, "inserted": 0}

        logger.info(f"Historical ingestion pipeline completed for {len(results)} assets.")
        return results

    def run_latest_prices_update(self) -> Dict[str, bool]:
        """
        Pulls latest price candles for all active assets.
        """
        logger.info("Starting latest market prices update pipeline...")
        all_assets = self.asset_repo.get_all_active()
        results = {}

        for asset in all_assets:
            try:
                success = self.collector.collect_latest_for_asset(asset)
                results[asset.symbol] = success
            except Exception as e:
                logger.error(f"Failed to update latest price for {asset.symbol}: {e}")
                results[asset.symbol] = False

        logger.info(f"Latest prices update pipeline completed: {sum(results.values())}/{len(results)} successful.")
        return results
