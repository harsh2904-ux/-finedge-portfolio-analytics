"""
Portfolio Valuation Engine: Computes current and historical portfolio values, actual weights, and P&L
"""

from datetime import datetime, timezone
from typing import Dict, List, Tuple
import pandas as pd
from pydantic import BaseModel
from portfolio_analytics.portfolio.holdings import HoldingValuation


class PortfolioSnapshot(BaseModel):
    """Overall current valuation snapshot of a portfolio."""
    portfolio_id: int
    name: str
    base_currency: str
    total_market_value: float
    total_cost_basis: float
    total_unrealized_pnl: float
    total_unrealized_pnl_pct: float
    holdings_count: int
    as_of_date: datetime
    positions: List[HoldingValuation]


class PortfolioValuationEngine:
    """
    Engine to compute real-time and historical portfolio valuations
    from actual quantities and prices (not just target weights).
    """

    @staticmethod
    def calculate_current_snapshot(
        portfolio_id: int,
        portfolio_name: str,
        base_currency: str,
        holdings_data: List[dict],
        latest_prices: Dict[str, float],
    ) -> PortfolioSnapshot:
        """
        Calculates current portfolio market value, actual weights, and P&L.
        """
        positions: List[HoldingValuation] = []
        total_market_value = 0.0
        total_cost_basis = 0.0

        # Step 1: Calculate market values
        for h in holdings_data:
            sym = h["symbol"]
            qty = float(h["quantity"])
            avg_cost = float(h["average_cost"])
            cur_price = float(latest_prices.get(sym, avg_cost))

            mkt_val = qty * cur_price
            cost_basis = qty * avg_cost

            total_market_value += mkt_val
            total_cost_basis += cost_basis

        # Step 2: Calculate actual weights and build positions
        for h in holdings_data:
            sym = h["symbol"]
            qty = float(h["quantity"])
            avg_cost = float(h["average_cost"])
            cur_price = float(latest_prices.get(sym, avg_cost))

            mkt_val = qty * cur_price
            cost_basis = qty * avg_cost
            pnl = mkt_val - cost_basis
            pnl_pct = (pnl / cost_basis * 100.0) if cost_basis > 0 else 0.0
            actual_weight = (mkt_val / total_market_value) if total_market_value > 0 else 0.0

            positions.append(
                HoldingValuation(
                    symbol=sym,
                    company_name=h.get("name", sym),
                    asset_class=h.get("asset_class", "Equity"),
                    sector=h.get("sector"),
                    quantity=qty,
                    average_cost=avg_cost,
                    current_price=round(cur_price, 4),
                    current_market_value=round(mkt_val, 2),
                    total_cost_basis=round(cost_basis, 2),
                    unrealized_pnl=round(pnl, 2),
                    unrealized_pnl_pct=round(pnl_pct, 2),
                    actual_weight=round(actual_weight, 4),
                    target_weight=h.get("target_weight"),
                    currency=base_currency,
                )
            )

        total_pnl = total_market_value - total_cost_basis
        total_pnl_pct = (total_pnl / total_cost_basis * 100.0) if total_cost_basis > 0 else 0.0

        return PortfolioSnapshot(
            portfolio_id=portfolio_id,
            name=portfolio_name,
            base_currency=base_currency,
            total_market_value=round(total_market_value, 2),
            total_cost_basis=round(total_cost_basis, 2),
            total_unrealized_pnl=round(total_pnl, 2),
            total_unrealized_pnl_pct=round(total_pnl_pct, 2),
            holdings_count=len(positions),
            as_of_date=datetime.now(timezone.utc),
            positions=positions,
        )

    @staticmethod
    def calculate_historical_series(
        price_matrix: pd.DataFrame,
        quantities: Dict[str, float],
    ) -> pd.Series:
        """
        Computes historical total portfolio value series across time:
        V_t = Sum(Quantity_i * Price_i,t)
        """
        if price_matrix.empty or not quantities:
            return pd.Series(dtype=float)

        # Align columns with quantities
        active_symbols = [s for s in price_matrix.columns if s in quantities and quantities[s] > 0]
        if not active_symbols:
            return pd.Series(dtype=float)

        sub_matrix = price_matrix[active_symbols]
        qty_series = pd.Series({s: quantities[s] for s in active_symbols})

        portfolio_values = sub_matrix.dot(qty_series)
        portfolio_values.name = "portfolio_value"
        return portfolio_values
