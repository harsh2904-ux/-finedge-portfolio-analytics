"""
High-Level Portfolio Management Service
"""

from datetime import datetime, timezone
from typing import Dict, List, Optional
import pandas as pd
from sqlalchemy.orm import Session
from portfolio_analytics.database.repositories.portfolio_repository import PortfolioRepository
from portfolio_analytics.database.repositories.asset_repository import AssetRepository
from portfolio_analytics.database.repositories.price_repository import PriceRepository
from portfolio_analytics.portfolio.valuation import PortfolioValuationEngine, PortfolioSnapshot
from portfolio_analytics.config.logging_config import logger


class PortfolioService:
    """
    Coordinates portfolio management, holdings maintenance, and valuation queries.
    """

    def __init__(self, session: Session):
        self.session = session
        self.portfolio_repo = PortfolioRepository(session)
        self.asset_repo = AssetRepository(session)
        self.price_repo = PriceRepository(session)

    def create_portfolio(
        self,
        name: str,
        base_currency: str = "INR",
        benchmark_symbol: Optional[str] = "NIFTYBEES",
    ):
        benchmark_asset = self.asset_repo.get_by_symbol(benchmark_symbol) if benchmark_symbol else None
        benchmark_id = benchmark_asset.id if benchmark_asset else None

        portfolio = self.portfolio_repo.create_portfolio(
            name=name,
            base_currency=base_currency,
            benchmark_asset_id=benchmark_id,
        )
        self.session.commit()
        logger.info(f"Created portfolio '{name}' (ID={portfolio.id}) with benchmark '{benchmark_symbol}'")
        return portfolio

    def add_position(
        self,
        portfolio_id: int,
        symbol: str,
        quantity: float,
        price: float,
        target_weight: Optional[float] = None,
        date: Optional[datetime] = None,
    ):
        asset = self.asset_repo.get_by_symbol(symbol)
        if not asset:
            raise ValueError(f"Asset with symbol '{symbol}' not found in database.")

        tx_date = date or datetime.now(timezone.utc)
        # Record transaction
        self.portfolio_repo.record_transaction(
            portfolio_id=portfolio_id,
            asset_id=asset.id,
            transaction_type="BUY",
            quantity=quantity,
            price=price,
            transaction_date=tx_date,
        )
        # Update holding
        holding = self.portfolio_repo.add_or_update_holding(
            portfolio_id=portfolio_id,
            asset_id=asset.id,
            quantity=quantity,
            average_cost=price,
            target_weight=target_weight,
            purchase_date=tx_date,
        )
        self.session.commit()
        logger.info(f"Added position {quantity} shares of {symbol} @ {price} to portfolio ID={portfolio_id}")
        return holding

    def get_current_snapshot(self, portfolio_id: int) -> PortfolioSnapshot:
        portfolio = self.portfolio_repo.get_portfolio(portfolio_id)
        if not portfolio:
            raise ValueError(f"Portfolio ID={portfolio_id} not found.")

        holdings_data = []
        latest_prices = {}

        for h in portfolio.holdings:
            sym = h.asset.symbol
            holdings_data.append({
                "symbol": sym,
                "name": h.asset.name,
                "asset_class": h.asset.asset_class,
                "sector": h.asset.sector,
                "quantity": h.quantity,
                "average_cost": h.average_cost,
                "target_weight": h.target_weight,
            })
            latest_price_rec = self.price_repo.get_latest_price(h.asset_id)
            latest_prices[sym] = latest_price_rec.close if latest_price_rec else h.average_cost

        return PortfolioValuationEngine.calculate_current_snapshot(
            portfolio_id=portfolio.id,
            portfolio_name=portfolio.name,
            base_currency=portfolio.base_currency,
            holdings_data=holdings_data,
            latest_prices=latest_prices,
        )

    def get_historical_portfolio_and_benchmark(
        self,
        portfolio_id: int,
        start_date: Optional[datetime] = None,
        end_date: Optional[datetime] = None,
    ) -> Tuple[pd.Series, pd.Series, pd.DataFrame]:
        """
        Retrieves aligned historical series for:
        1. Portfolio total value series
        2. Benchmark price series
        3. Underlying asset price matrix
        """
        portfolio = self.portfolio_repo.get_portfolio(portfolio_id)
        if not portfolio:
            raise ValueError(f"Portfolio ID={portfolio_id} not found.")

        asset_ids = [h.asset_id for h in portfolio.holdings]
        if portfolio.benchmark_asset_id and portfolio.benchmark_asset_id not in asset_ids:
            asset_ids.append(portfolio.benchmark_asset_id)

        price_matrix = self.price_repo.get_aligned_price_matrix(
            asset_ids=asset_ids,
            start_date=start_date,
            end_date=end_date,
        )

        quantities = {h.asset.symbol: h.quantity for h in portfolio.holdings}
        portfolio_series = PortfolioValuationEngine.calculate_historical_series(price_matrix, quantities)

        # Benchmark series
        benchmark_symbol = portfolio.benchmark_asset.symbol if portfolio.benchmark_asset else None
        if benchmark_symbol and benchmark_symbol in price_matrix.columns:
            benchmark_series = price_matrix[benchmark_symbol]
        else:
            benchmark_series = pd.Series(dtype=float)

        return portfolio_series, benchmark_series, price_matrix
