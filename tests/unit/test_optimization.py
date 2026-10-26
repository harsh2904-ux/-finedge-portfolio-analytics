"""
Unit Tests for Portfolio Optimization (Min Variance, Max Sharpe, Efficient Frontier)
"""

import numpy as np
from portfolio_analytics.optimization.minimum_variance import MinimumVarianceOptimizer
from portfolio_analytics.optimization.maximum_sharpe import MaximumSharpeOptimizer
from portfolio_analytics.optimization.efficient_frontier import EfficientFrontierEngine


def test_minimum_variance_constraints(sample_price_matrix):
    res = MinimumVarianceOptimizer.optimize(sample_price_matrix, max_weight=1.0)
    weights = list(res.weights.values())

    # Constraints: sum to 1.0, non-negative
    assert np.isclose(sum(weights), 1.0, atol=1e-3)
    assert all(w >= -1e-4 for w in weights)
    assert res.expected_annual_volatility > 0


def test_maximum_sharpe_constraints(sample_price_matrix):
    res = MaximumSharpeOptimizer.optimize(sample_price_matrix, risk_free_rate=0.04)
    weights = list(res.weights.values())

    assert np.isclose(sum(weights), 1.0, atol=1e-3)
    assert all(w >= -1e-4 for w in weights)


def test_efficient_frontier_generation(sample_price_matrix):
    res = EfficientFrontierEngine.generate_frontier(sample_price_matrix, num_portfolios=500, random_seed=42)
    assert res.total_simulated == 500
    assert len(res.frontier_sample) > 0
    assert res.min_volatility_portfolio.expected_annual_volatility <= res.max_sharpe_portfolio.expected_annual_volatility + 1e-4
