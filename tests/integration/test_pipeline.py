"""
Integration Tests for End-to-End Market Data Provider -> Pipeline -> Analytics
"""

from unittest.mock import MagicMock
from datetime import datetime, timezone, timedelta
from portfolio_analytics.market_data.base_provider import MarketDataProvider, RawPriceRecord
from portfolio_analytics.pipelines.ingestion_pipeline import IngestionPipeline
from portfolio_analytics.pipelines.analytics_pipeline import AnalyticsPipeline
from portfolio_analytics.database.repositories.asset_repository import AssetRepository
from portfolio_analytics.portfolio.portfolio_service import PortfolioService


class MockMarketDataProvider(MarketDataProvider):
    """Mock provider simulating Twelve Data responses."""

    def fetch_historical_prices(self, symbol: str, start_date=None, end_date=None, interval="1day"):
        base_prices = {"AAPL": 150.0, "SPY": 400.0, "BND": 75.0}
        base_p = base_prices.get(symbol, 100.0)

        records = []
        start = datetime(2024, 1, 1, tzinfo=timezone.utc)
        for i in range(50):
            d = start + timedelta(days=i)
            p = base_p * (1.0 + 0.001 * i)
            records.append(
                RawPriceRecord(
                    symbol=symbol,
                    timestamp=d,
                    open=p * 0.99,
                    high=p * 1.02,
                    low=p * 0.98,
                    close=p,
                    volume=100000.0,
                    source="mock_twelve_data",
                )
            )
        return records

    def fetch_latest_price(self, symbol: str):
        history = self.fetch_historical_prices(symbol)
        return history[-1] if history else None

    def fetch_quote(self, symbol: str):
        return None


def test_full_ingestion_and_analytics_pipeline_flow(db_session):
    # 1. Setup Assets
    asset_repo = AssetRepository(db_session)
    asset_aapl = asset_repo.create_or_update("AAPL", "AAPL", "Apple Inc.", "Equity")
    asset_bnd = asset_repo.create_or_update("BND", "BND", "Vanguard Bond", "Fixed Income / Bond ETF")
    asset_spy = asset_repo.create_or_update("SPY", "SPY", "S&P 500 ETF", "Equity / ETF")
    db_session.commit()

    # 2. Ingestion Pipeline
    provider = MockMarketDataProvider()
    ingestion = IngestionPipeline(provider, db_session)
    results = ingestion.run_historical_ingestion(symbols=["AAPL", "BND", "SPY"])

    assert results["AAPL"]["inserted"] == 50
    assert results["BND"]["inserted"] == 50
    assert results["SPY"]["inserted"] == 50

    # 3. Create Portfolio & Positions
    port_service = PortfolioService(db_session)
    portfolio = port_service.create_portfolio("Tech-Bond Mix", base_currency="USD", benchmark_symbol="SPY")
    port_service.add_position(portfolio.id, "AAPL", quantity=100, price=150.0)
    port_service.add_position(portfolio.id, "BND", quantity=200, price=75.0)

    # 4. Analytics Pipeline Execution
    analytics = AnalyticsPipeline(db_session)
    report = analytics.run_portfolio_analytics(portfolio.id, mc_simulations=500, ef_simulations=200)

    assert report.snapshot.holdings_count == 2
    assert report.snapshot.total_market_value > 0
    assert report.performance.total_return is not None
    assert report.risk.annualized_volatility >= 0
    assert report.risk.sharpe_ratio is not None
    assert report.monte_carlo.num_simulations == 500
    assert len(report.stress_tests) == 4
