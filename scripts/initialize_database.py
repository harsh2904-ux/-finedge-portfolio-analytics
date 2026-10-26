"""
CLI Script: Initialize Database and Seed Default Assets
Usage:
    python scripts/initialize_database.py
"""

import sys
from pathlib import Path

# Add src to sys.path
sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "src"))

from portfolio_analytics.database.connection import create_all_tables, SessionFactory
from portfolio_analytics.database.repositories.asset_repository import AssetRepository
from portfolio_analytics.config.logging_config import logger


def main():
    logger.info("Initializing database tables...")
    create_all_tables()

    session = SessionFactory()
    try:
        asset_repo = AssetRepository(session)
        logger.info("Seeding default multi-asset watchlist...")
        seeded = asset_repo.seed_from_config("config/default_assets.json")
        logger.info(f"Database successfully initialized with {len(seeded)} active assets.")
    finally:
        session.close()


if __name__ == "__main__":
    main()
