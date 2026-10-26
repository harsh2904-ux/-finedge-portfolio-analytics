"""
Structured Pydantic Models for Optimization and Monte Carlo Results
"""

from typing import Dict, List, Optional
from pydantic import BaseModel, Field


class OptimizationResult(BaseModel):
    """Result of an asset allocation optimization."""
    method: str = Field(..., description="Optimization technique (Equal Weight, Min Volatility, Max Sharpe)")
    weights: Dict[str, float] = Field(..., description="Optimized asset weights")
    expected_annual_return: float = Field(..., description="Annualized portfolio return")
    expected_annual_volatility: float = Field(..., description="Annualized portfolio volatility")
    sharpe_ratio: float = Field(..., description="Portfolio Sharpe ratio")
    constraints: Dict[str, float] = Field(default_factory=dict, description="Constraints applied (max_weight, etc.)")


class EfficientFrontierPoint(BaseModel):
    """Single portfolio on the efficient frontier."""
    expected_return: float
    volatility: float
    sharpe_ratio: float
    weights: Dict[str, float]


class EfficientFrontierResult(BaseModel):
    """Collection of simulated portfolios forming the Efficient Frontier."""
    total_simulated: int
    min_volatility_portfolio: OptimizationResult
    max_sharpe_portfolio: OptimizationResult
    frontier_sample: List[EfficientFrontierPoint]


class MonteCarloResult(BaseModel):
    """Stochastic simulation trajectory results for portfolio valuation."""
    num_simulations: int
    time_horizon_days: int
    initial_value: float
    mean_ending_value: float
    median_ending_value: float
    std_ending_value: float
    p5_ending_value: float
    p25_ending_value: float
    p75_ending_value: float
    p95_ending_value: float
    probability_of_loss: float
    simulation_paths_sample: List[List[float]]
