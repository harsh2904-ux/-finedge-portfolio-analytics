"""
Integration Tests for Database Repositories, Constraints, and Foreign Keys
"""

import pytest
from datetime import datetime, timezone
from portfolio_analytics.database.repositories.asset_repository import AssetRepository
from portfolio_analytics.database.repositories.price_repository import PriceRepository
from portfolio_analytics.database.repositories.portfolio_repository import PortfolioRepository


def test_asset_and_price_repository_integration(db_session):
    asset_repo = AssetRepository(db_session)
    price_repo = PriceRepository(db_session)

    # 1. Create asset
    asset = asset_repo.create_or_update(
        symbol="TEST",
        provider_symbol="TEST",
        name="Test Corp",
        asset_class="Equity",
        currency="USD",
    )
    db_session.commit()
    assert asset.id is not None

    # 2. Insert market prices
    dt = datetime(2024, 1, 1, 10, 0, 0, tzinfo=timezone.utc)
    records = [{
        "asset_id": asset.id,
        "timestamp": dt,
        "open": 100.0,
        "high": 105.0,
        "low": 99.0,
        "close": 103.0,
        "volume": 50000.0,
        "source": "twelve_data",
    }]
    count = price_repo.insert_prices(records)
    db_session.commit()
    assert count == 1

    # 3. Duplicate insertion test
    dup_count = price_repo.insert_prices(records)
    db_session.commit()
    assert dup_count == 0  # Deduplicated!

    # 4. Fetch latest price
    latest = price_repo.get_latest_price(asset.id)
    assert latest is not None
    assert latest.close == 103.0


def test_portfolio_holdings_and_transactions_integration(db_session):
    asset_repo = AssetRepository(db_session)
    port_repo = PortfolioRepository(db_session)

    asset = asset_repo.create_or_update(
        symbol="MSFT",
        provider_symbol="MSFT",
        name="Microsoft Corp",
        asset_class="Equity",
    )
    db_session.commit()

    # Create portfolio
    portfolio = port_repo.create_portfolio("Retirement Fund", base_currency="USD")
    db_session.commit()
    assert portfolio.id is not None

    # Add holding
    holding = port_repo.add_or_update_holding(
        portfolio_id=portfolio.id,
        asset_id=asset.id,
        quantity=50.0,
        average_cost=300.0,
        target_weight=1.0,
    )
    db_session.commit()
    assert holding.id is not None
    assert holding.quantity == 50.0

    # Record transaction
    tx = port_repo.record_transaction(
        portfolio_id=portfolio.id,
        asset_id=asset.id,
        transaction_type="BUY",
        quantity=50.0,
        price=300.0,
    )
    db_session.commit()
    assert tx.id is not None
