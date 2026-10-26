"""
Structured Pydantic Models for Financial and Quantitative Analytics Results
"""

from typing import Dict, List, Optional
from pydantic import BaseModel, Field


class PerformanceMetrics(BaseModel):
    """Portfolio Performance Analytics."""
    total_return: float = Field(..., description="Total cumulative return across period")
    cagr: float = Field(..., description="Compound Annual Growth Rate")
    annualized_return: float = Field(..., description="Annualized geometric return")
    benchmark_return: Optional[float] = Field(None, description="Benchmark cumulative return")
    excess_return: Optional[float] = Field(None, description="Return above benchmark")
    tracking_difference: Optional[float] = Field(None, description="Portfolio return minus benchmark return")
    tracking_error: Optional[float] = Field(None, description="Annualized standard deviation of excess returns")
    information_ratio: Optional[float] = Field(None, description="Excess return divided by tracking error")


class DrawdownResult(BaseModel):
    """Drawdown Analysis Details."""
    max_drawdown: float = Field(..., description="Maximum peak-to-trough drop")
    peak_date: Optional[str] = None
    trough_date: Optional[str] = None
    recovery_date: Optional[str] = None
    recovery_duration_days: Optional[int] = None
    is_recovered: bool = False


class RiskMetrics(BaseModel):
    """Comprehensive Quantitative Risk Metrics."""
    daily_volatility: float
    annualized_volatility: float
    sharpe_ratio: float
    sortino_ratio: float
    max_drawdown: float
    peak_date: Optional[str] = None
    trough_date: Optional[str] = None
    recovery_date: Optional[str] = None
    recovery_duration_days: Optional[int] = None
    var_historical_95: float
    var_historical_99: float
    var_parametric_95: float
    var_parametric_99: float
    var_monetary_95: float
    var_monetary_99: float
    expected_shortfall_95: float
    expected_shortfall_99: float
    beta: Optional[float] = None
    alpha: Optional[float] = None
    confidence_level: float = 0.95
    risk_free_rate: float = 0.04
    trading_days: int = 252


class CorrelationResult(BaseModel):
    """Asset Diversification and Covariance Matrix."""
    assets: List[str]
    correlation_matrix: Dict[str, Dict[str, float]]
    covariance_matrix: Dict[str, Dict[str, float]]
    lookback_period: str


class RiskAttributionResult(BaseModel):
    """Euler Decomposition of Portfolio Volatility."""
    portfolio_volatility: float
    marginal_contribution_to_risk: Dict[str, float]
    component_contribution_to_risk: Dict[str, float]
    percentage_risk_contribution: Dict[str, float]
