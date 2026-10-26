"""
Unit Tests for Monte Carlo Portfolio Simulation Engine
"""

from portfolio_analytics.optimization.monte_carlo import MonteCarloEngine


def test_monte_carlo_percentile_order_and_reproducibility(sample_price_matrix):
    weights = {"AAPL": 0.4, "BND": 0.3, "NVDA": 0.3}
    res1 = MonteCarloEngine.simulate(
        sample_price_matrix,
        weights=weights,
        initial_value=100000.0,
        time_horizon_days=50,
        num_simulations=1000,
        random_seed=42,
    )
    res2 = MonteCarloEngine.simulate(
        sample_price_matrix,
        weights=weights,
        initial_value=100000.0,
        time_horizon_days=50,
        num_simulations=1000,
        random_seed=42,
    )

    # Reproducibility check
    assert res1.mean_ending_value == res2.mean_ending_value
    assert res1.p5_ending_value == res2.p5_ending_value

    # Percentile ordering: p5 <= p25 <= median <= p75 <= p95
    assert res1.p5_ending_value <= res1.p25_ending_value
    assert res1.p25_ending_value <= res1.median_ending_value
    assert res1.median_ending_value <= res1.p75_ending_value
    assert res1.p75_ending_value <= res1.p95_ending_value

    # Probability of loss bounded in [0, 1]
    assert 0.0 <= res1.probability_of_loss <= 1.0
