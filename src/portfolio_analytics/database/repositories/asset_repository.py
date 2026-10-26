"""
Asset Repository for database operations on financial assets
"""

import json
from pathlib import Path
from typing import List, Optional
from sqlalchemy import select
from sqlalchemy.orm import Session
from portfolio_analytics.database.models import Asset
from portfolio_analytics.config.logging_config import logger


class AssetRepository:
    """Repository handling Asset queries and persistence."""

    def __init__(self, session: Session):
        self.session = session

    def get_by_id(self, asset_id: int) -> Optional[Asset]:
        return self.session.get(Asset, asset_id)

    def get_by_symbol(self, symbol: str) -> Optional[Asset]:
        stmt = select(Asset).where(Asset.symbol == symbol)
        return self.session.scalar(stmt)

    def get_all_active(self) -> List[Asset]:
        stmt = select(Asset).where(Asset.is_active == True).order_by(Asset.symbol)
        return list(self.session.scalars(stmt).all())

    def create_or_update(
        self,
        symbol: str,
        provider_symbol: str,
        name: str,
        asset_class: str,
        sector: Optional[str] = None,
        currency: str = "INR",
        exchange: Optional[str] = None,
        country: Optional[str] = None,
        is_active: bool = True,
    ) -> Asset:
        asset = self.get_by_symbol(symbol)
        if asset:
            asset.provider_symbol = provider_symbol
            asset.name = name
            asset.asset_class = asset_class
            asset.sector = sector
            asset.currency = currency
            asset.exchange = exchange
            asset.country = country
            asset.is_active = is_active
        else:
            asset = Asset(
                symbol=symbol,
                provider_symbol=provider_symbol,
                name=name,
                asset_class=asset_class,
                sector=sector,
                currency=currency,
                exchange=exchange,
                country=country,
                is_active=is_active,
            )
            self.session.add(asset)
        self.session.flush()
        return asset

    def seed_from_config(self, config_path: str = "config/default_assets.json") -> List[Asset]:
        """Loads and seeds default assets from JSON config file."""
        path = Path(config_path)
        if not path.exists():
            logger.warning(f"Assets config file not found at {config_path}")
            return []

        with open(path, "r", encoding="utf-8") as f:
            items = json.load(f)

        seeded = []
        for item in items:
            asset = self.create_or_update(
                symbol=item["symbol"],
                provider_symbol=item.get("provider_symbol", item["symbol"]),
                name=item["name"],
                asset_class=item["asset_class"],
                sector=item.get("sector"),
                currency=item.get("currency", "INR"),
                exchange=item.get("exchange"),
                country=item.get("country"),
                is_active=item.get("is_active", True),
            )
            seeded.append(asset)
        self.session.commit()
        logger.info(f"Seeded {len(seeded)} assets from {config_path}")
        return seeded
