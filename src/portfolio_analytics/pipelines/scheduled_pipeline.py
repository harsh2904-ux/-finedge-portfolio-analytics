"""
Scheduled Market Data Collection Pipeline using APScheduler
"""

import time
from typing import Optional
from apscheduler.schedulers.background import BackgroundScheduler
from portfolio_analytics.database.connection import SessionFactory
from portfolio_analytics.market_data.twelve_data_provider import TwelveDataProvider
from portfolio_analytics.pipelines.ingestion_pipeline import IngestionPipeline
from portfolio_analytics.config.settings import settings
from portfolio_analytics.config.logging_config import logger


class ScheduledPipelineRunner:
    """
    Runs periodic data collection jobs in the background.
    """

    def __init__(self, interval_minutes: Optional[int] = None):
        self.interval_minutes = interval_minutes or settings.DATA_UPDATE_INTERVAL_MINUTES
        self.scheduler = BackgroundScheduler()
        self.provider = TwelveDataProvider()

    def job_update_latest_prices(self):
        """Job executed every interval_minutes to refresh active market prices."""
        logger.info(f"Executing scheduled latest market price refresh...")
        session = SessionFactory()
        try:
            pipeline = IngestionPipeline(self.provider, session)
            results = pipeline.run_latest_prices_update()
            successes = sum(1 for v in results.values() if v)
            logger.info(f"Scheduled market data update complete: {successes}/{len(results)} updated.")
        except Exception as e:
            logger.error(f"Error during scheduled market data update: {e}")
        finally:
            session.close()

    def start(self):
        """Starts the background scheduler."""
        logger.info(f"Starting APScheduler with interval={self.interval_minutes} minutes...")
        self.scheduler.add_job(
            self.job_update_latest_prices,
            "interval",
            minutes=self.interval_minutes,
            id="market_data_refresh_job",
            replace_existing=True,
        )
        self.scheduler.start()

    def stop(self):
        """Stops the scheduler."""
        if self.scheduler.running:
            self.scheduler.shutdown()
            logger.info("APScheduler stopped.")
