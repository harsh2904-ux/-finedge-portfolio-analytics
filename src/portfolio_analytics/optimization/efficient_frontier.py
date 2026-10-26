"""
Efficient Frontier Generator (Monte Carlo Simulation & Boundary Tracing)
"""

from typing import List, Optional
import numpy as np
import pandas as pd
from portfolio_analytics.optimization.models import (
    EfficientFrontierPoint,
    EfficientFrontierResult,
)
from portfolio_analytics.optimization.minimum_variance import MinimumVarianceOptimizer
from portfolio_analytics.optimization.maximum_sharpe import MaximumSharpeOptimizer


class EfficientFrontierEngine:
    """
    Simulates thousands of portfolio allocations to generate the Markowitz Efficient Frontier.
    """

    @staticmethod
    def generate_frontier(
        price_matrix: pd.DataFrame,
        num_portfolios: int = 5000,
        risk_free_rate: float = 0.04,
        random_seed: Optional[int] = 42,
    ) -> EfficientFrontierResult:
        if random_seed is not None:
            np.random.seed(random_seed)

        returns_df = price_matrix.pct_change().dropna()
        symbols = list(returns_df.columns)
        num_assets = len(symbols)

        if num_assets < 2:
            min_var = MinimumVarianceOptimizer.optimize(price_matrix, risk_free_rate=risk_free_rate)
            max_sharpe = MaximumSharpeOptimizer.optimize(price_matrix, risk_free_rate=risk_free_rate)
            return EfficientFrontierResult(
                total_simulated=1,
                min_volatility_portfolio=min_var,
                max_sharpe_portfolio=max_sharpe,
                frontier_sample=[],
            )

        mean_returns = returns_df.mean().values * 252.0
        cov_matrix = (returns_df.cov() * 252.0).values

        # Generate Dirichlet random weights (uniform across the simplex)
        weights_sim = np.random.dirichlet(np.ones(num_assets), size=num_portfolios)

        # Vectorized portfolio returns and volatilities
        port_returns = np.dot(weights_sim, mean_returns)
        port_vars = np.einsum("ij,jk,ik->i", weights_sim, cov_matrix, weights_sim)
        port_vols = np.sqrt(np.maximum(0.0, port_vars))
        sharpe_ratios = np.where(port_vols > 0, (port_returns - risk_free_rate) / port_vols, 0.0)

        # Precise optimal portfolios using analytical SLSQP
        min_var = MinimumVarianceOptimizer.optimize(price_matrix, risk_free_rate=risk_free_rate)
        max_sharpe = MaximumSharpeOptimizer.optimize(price_matrix, risk_free_rate=risk_free_rate)

        # Sample 200 representative points for frontend charting (evenly spaced by volatility)
        sorted_indices = np.argsort(port_vols)
        sample_step = max(1, num_portfolios // 200)
        sampled_indices = sorted_indices[::sample_step]

        frontier_points: List[EfficientFrontierPoint] = []
        for idx in sampled_indices:
            w_dict = {symbols[j]: round(float(weights_sim[idx, j]), 4) for j in range(num_assets)}
            frontier_points.append(
                EfficientFrontierPoint(
                    expected_return=round(float(port_returns[idx]), 6),
                    volatility=round(float(port_vols[idx]), 6),
                    sharpe_ratio=round(float(sharpe_ratios[idx]), 4),
                    weights=w_dict,
                )
            )

        return EfficientFrontierResult(
            total_simulated=num_portfolios,
            min_volatility_portfolio=min_var,
            max_sharpe_portfolio=max_sharpe,
            frontier_sample=frontier_points,
        )
