"""
Maximum Sharpe Ratio Portfolio Optimization
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


def _optimize_max_sharpe_numpy(mean_returns: np.ndarray, cov_matrix: np.ndarray, risk_free_rate: float, num_assets: int, max_weight: float = 1.0) -> np.ndarray:
    samples = np.random.dirichlet(np.ones(num_assets), size=5000)
    samples = np.clip(samples, 0.0, max_weight)
    samples = samples / samples.sum(axis=1, keepdims=True)
    returns = np.dot(samples, mean_returns)
    variances = np.sum(samples * np.dot(samples, cov_matrix), axis=1)
    vols = np.sqrt(np.maximum(variances, 1e-12))
    sharpes = (returns - risk_free_rate) / vols
    best_idx = int(np.argmax(sharpes))
    w = samples[best_idx].copy()

    lr = 0.01
    for _ in range(500):
        port_ret = float(np.dot(w, mean_returns))
        port_var = float(np.dot(w.T, np.dot(cov_matrix, w)))
        port_vol = float(np.sqrt(max(port_var, 1e-12)))
        excess = port_ret - risk_free_rate
        grad = (mean_returns * port_vol - (excess / port_vol) * np.dot(cov_matrix, w)) / (port_var + 1e-8)
        w = w + lr * grad
        w = np.clip(w, 0.0, max_weight)
        s = float(np.sum(w))
        if s > 0:
            w = w / s
    return w


class MaximumSharpeOptimizer:
    """
    Finds the optimal tangency portfolio that maximizes Sharpe ratio using Scipy SLSQP.
    """

    @staticmethod
    def optimize(
        price_matrix: pd.DataFrame,
        risk_free_rate: float = 0.04,
        max_weight: float = 1.0,
    ) -> OptimizationResult:
        returns_df = price_matrix.pct_change().dropna()
        symbols = list(returns_df.columns)
        num_assets = len(symbols)

        if num_assets < 2:
            return OptimizationResult(
                method="Maximum Sharpe",
                weights={s: 1.0 for s in symbols},
                expected_annual_return=0.0,
                expected_annual_volatility=0.0,
                sharpe_ratio=0.0,
            )

        mean_returns = returns_df.mean() * 252.0
        cov_matrix = returns_df.cov() * 252.0

        def negative_sharpe(w):
            port_ret = np.dot(w, mean_returns)
            port_vol = np.sqrt(np.dot(w.T, np.dot(cov_matrix.values, w)))
            if port_vol <= 0:
                return 0.0
            return - (port_ret - risk_free_rate) / port_vol

        constraints = {"type": "eq", "fun": lambda w: np.sum(w) - 1.0}
        bounds = tuple((0.0, max_weight) for _ in range(num_assets))
        init_guess = np.ones(num_assets) / num_assets

        if HAS_SCIPY and minimize is not None:
            try:
                res = minimize(
                    negative_sharpe,
                    init_guess,
                    method="SLSQP",
                    bounds=bounds,
                    constraints=constraints,
                )
                opt_weights = res.x
            except Exception:
                opt_weights = _optimize_max_sharpe_numpy(mean_returns.values, cov_matrix.values, risk_free_rate, num_assets, max_weight)
        else:
            opt_weights = _optimize_max_sharpe_numpy(mean_returns.values, cov_matrix.values, risk_free_rate, num_assets, max_weight)

        exp_ret = float(np.dot(opt_weights, mean_returns))
        exp_vol = float(np.sqrt(np.dot(opt_weights.T, np.dot(cov_matrix.values, opt_weights))))
        sharpe = (exp_ret - risk_free_rate) / exp_vol if exp_vol > 0 else 0.0

        weight_dict = {symbols[i]: round(float(opt_weights[i]), 4) for i in range(num_assets)}

        return OptimizationResult(
            method="Maximum Sharpe",
            weights=weight_dict,
            expected_annual_return=round(exp_ret, 6),
            expected_annual_volatility=round(exp_vol, 6),
            sharpe_ratio=round(sharpe, 4),
            constraints={"max_weight": max_weight, "sum_weights": 1.0, "risk_free_rate": risk_free_rate},
        )
