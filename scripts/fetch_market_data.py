"""
CLI Script: Fetch Market Data from Twelve Data
Usage:
    python scripts/fetch_market_data.py --symbol AAPL --historical
    python scripts/fetch_market_data.py --all --historical
    python scripts/fetch_market_data.py --all --latest
"""

import argparse
import sys
from datetime import datetime, timedelta, timezone
from pathlib import Path

# Add src to sys.path
sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "src"))

from portfolio_analytics.database.connection import SessionFactory
from portfolio_analytics.market_data.twelve_data_provider import TwelveDataProvider
from portfolio_analytics.pipelines.ingestion_pipeline import IngestionPipeline
from portfolio_analytics.config.settings import settings
from portfolio_analytics.config.logging_config import logger


def parse_args():
    parser = argparse.ArgumentParser(description="Fetch market data from Twelve Data into PostgreSQL")
    parser.add_argument("--symbol", type=str, help="Single asset symbol to fetch (e.g. AAPL, SPY)")
    parser.add_argument("--all", action="store_true", help="Fetch for all active assets in watchlist")
    parser.add_argument("--historical", action="store_true", help="Fetch multi-year historical candles")
    parser.add_argument("--latest", action="store_true", help="Fetch only latest available price")
    parser.add_argument("--start-date", type=str, help="Start date YYYY-MM-DD")
    parser.add_argument("--end-date", type=str, help="End date YYYY-MM-DD")
    parser.add_argument("--interval", type=str, default="1day", help="Candle interval (default: 1day)")
    return parser.parse_args()


def main():
    args = parse_args()
    if not settings.TWELVE_DATA_API_KEY:
        logger.warning(
            "TWELVE_DATA_API_KEY is not set in .env. "
            "Please set your Twelve Data API key in .env to fetch live data from the Twelve Data API."
        )

    start_dt = datetime.strptime(args.start_date, "%Y-%m-%d") if args.start_date else (datetime.now(timezone.utc) - timedelta(days=730))
    end_dt = datetime.strptime(args.end_date, "%Y-%m-%d") if args.end_date else datetime.now(timezone.utc)

    session = SessionFactory()
    provider = TwelveDataProvider()
    pipeline = IngestionPipeline(provider, session)

    try:
        symbols = [args.symbol] if args.symbol else None

        if args.historical or not args.latest:
            logger.info(f"Ingesting historical data from {start_dt.date()} to {end_dt.date()}...")
            results = pipeline.run_historical_ingestion(
                symbols=symbols,
                start_date=start_dt,
                end_date=end_dt,
                interval=args.interval,
            )
            print("\n================ INGESTION SUMMARY ================")
            for sym, res in results.items():
                print(f"  {sym:<8}: Fetched={res.get('fetched', 0):<5} Valid={res.get('valid', 0):<5} Inserted={res.get('inserted', 0):<5}")
            print("===================================================\n")

        if args.latest:
            logger.info("Ingesting latest market prices...")
            results = pipeline.run_latest_prices_update()
            print("\n================ LATEST UPDATE SUMMARY ================")
            for sym, success in results.items():
                status = "SUCCESS" if success else "FAILED"
                print(f"  {sym:<8}: {status}")
            print("=======================================================\n")

    finally:
        session.close()


if __name__ == "__main__":
    main()
