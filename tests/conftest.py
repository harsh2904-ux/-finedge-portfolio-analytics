"""
Pytest Fixtures for Unit and Integration Testing
"""

import pytest
import numpy as np
import pandas as pd
from datetime import datetime, timedelta
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from portfolio_analytics.database.connection import Base
from portfolio_analytics.database.models import Asset, MarketPrice, Portfolio, PortfolioHolding
from portfolio_analytics.portfolio.holdings import HoldingValuation


@pytest.fixture
def db_session():
    """In-memory SQLite database session for testing."""
    engine = create_engine("sqlite:///:memory:", echo=False)
    Base.metadata.create_all(bind=engine)
    TestingSessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False)
    session = TestingSessionLocal()
    try:
        yield session
    finally:
        session.close()


@pytest.fixture
def sample_price_series():
    """Generates a reproducible 100-day upward trending price series."""
    np.random.seed(42)
    start_date = datetime(2024, 1, 1)
    dates = [start_date + timedelta(days=i) for i in range(100)]
    returns = np.random.normal(0.001, 0.015, size=100)
    prices = [100.0]
    for r in returns[1:]:
        prices.append(prices[-1] * (1.0 + r))
    return pd.Series(prices, index=dates, name="test_price")


@pytest.fixture
def sample_price_matrix():
    """Generates an aligned 3-asset price matrix for 100 days."""
    np.random.seed(42)
    start_date = datetime(2024, 1, 1)
    dates = [start_date + timedelta(days=i) for i in range(100)]
    
    # 3 assets with different drift and volatility
    ret_a = np.random.normal(0.0008, 0.012, size=100)
    ret_b = np.random.normal(0.0004, 0.008, size=100)
    ret_c = np.random.normal(0.0015, 0.022, size=100)

    p_a = [100.0]
    p_b = [50.0]
    p_c = [200.0]

    for i in range(1, 100):
        p_a.append(p_a[-1] * (1.0 + ret_a[i]))
        p_b.append(p_b[-1] * (1.0 + ret_b[i]))
        p_c.append(p_c[-1] * (1.0 + ret_c[i]))

    return pd.DataFrame({"AAPL": p_a, "BND": p_b, "NVDA": p_c}, index=dates)


@pytest.fixture
def sample_positions():
    """Sample holding valuations for scenario and stress test testing."""
    return [
        HoldingValuation(
            symbol="AAPL",
            company_name="Apple Inc.",
            asset_class="Equity",
            sector="Technology",
            quantity=100.0,
            average_cost=150.0,
            current_price=180.0,
            current_market_value=18000.0,
            total_cost_basis=15000.0,
            unrealized_pnl=3000.0,
            unrealized_pnl_pct=20.0,
            actual_weight=0.60,
        ),
        HoldingValuation(
            symbol="BND",
            company_name="Vanguard Bond ETF",
            asset_class="Fixed Income / Bond ETF",
            sector="Bonds",
            quantity=150.0,
            average_cost=70.0,
            current_price=80.0,
            current_market_value=12000.0,
            total_cost_basis=10500.0,
            unrealized_pnl=1500.0,
            unrealized_pnl_pct=14.29,
            actual_weight=0.40,
        ),
    ]
