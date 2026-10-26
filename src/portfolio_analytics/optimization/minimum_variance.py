"""
Minimum Variance Portfolio Optimization
"""

from typing import Dict, List, Optional
import numpy as np
import pandas as pd
try:
    from scipy.optimize import minimize
    HAS_SCIPY = True
except Exception:
    HAS_SCIPY = False
    minimize = None

from portfolio_analytics.optimization.models import OptimizationResult


def _optimize_min_var_numpy(cov_matrix: np.ndarray, num_assets: int, max_weight: float = 1.0) -> np.ndarray:
    w = np.ones(num_assets) / num_assets
    lr = 0.1 / (np.trace(cov_matrix) + 1e-8)
    for _ in range(1000):
        grad = 2.0 * np.dot(cov_matrix, w)
        w = w - lr * grad
        w = np.clip(w, 0.0, max_weight)
        s = np.sum(w)
        if s > 0:
            w = w / s
    return w


class MinimumVarianceOptimizer:
    """
    Finds the global minimum variance portfolio using quadratic programming (SLSQP).
    """

    @staticmethod
    def optimize(
        price_matrix: pd.DataFrame,
        max_weight: float = 1.0,
        risk_free_rate: float = 0.04,
    ) -> OptimizationResult:
        returns_df = price_matrix.pct_change().dropna()
        symbols = list(returns_df.columns)
        num_assets = len(symbols)

        if num_assets < 2:
            return OptimizationResult(
                method="Minimum Variance",
                weights={s: 1.0 for s in symbols},
                expected_annual_return=0.0,
                expected_annual_volatility=0.0,
                sharpe_ratio=0.0,
            )

        mean_returns = returns_df.mean() * 252.0
        cov_matrix = returns_df.cov() * 252.0

        def portfolio_variance(w):
            return np.dot(w.T, np.dot(cov_matrix.values, w))

        # Constraints & bounds: sum(w) == 1, 0 <= w_i <= max_weight
        constraints = {"type": "eq", "fun": lambda w: np.sum(w) - 1.0}
        bounds = tuple((0.0, max_weight) for _ in range(num_assets))
        init_guess = np.ones(num_assets) / num_assets

        if HAS_SCIPY and minimize is not None:
            try:
                res = minimize(
                    portfolio_variance,
                    init_guess,
                    method="SLSQP",
                    bounds=bounds,
                    constraints=constraints,
                )
                opt_weights = res.x
            except Exception:
                opt_weights = _optimize_min_var_numpy(cov_matrix.values, num_assets, max_weight)
        else:
            opt_weights = _optimize_min_var_numpy(cov_matrix.values, num_assets, max_weight)

        exp_ret = float(np.dot(opt_weights, mean_returns))
        exp_vol = float(np.sqrt(portfolio_variance(opt_weights)))
        sharpe = (exp_ret - risk_free_rate) / exp_vol if exp_vol > 0 else 0.0

        weight_dict = {symbols[i]: round(float(opt_weights[i]), 4) for i in range(num_assets)}

        return OptimizationResult(
            method="Minimum Variance",
            weights=weight_dict,
            expected_annual_return=round(exp_ret, 6),
            expected_annual_volatility=round(exp_vol, 6),
            sharpe_ratio=round(sharpe, 4),
            constraints={"max_weight": max_weight, "sum_weights": 1.0},
        )
