"""
Holding Data Structures and P&L Calculations
"""

from typing import Optional
from pydantic import BaseModel, Field


class HoldingValuation(BaseModel):
    """Snapshot valuation of a single portfolio holding."""
    symbol: str
    company_name: str
    asset_class: str
    sector: Optional[str] = None
    quantity: float
    average_cost: float
    current_price: float
    current_market_value: float
    total_cost_basis: float
    unrealized_pnl: float
    unrealized_pnl_pct: float
    actual_weight: float
    target_weight: Optional[float] = None
    currency: str = "INR"
