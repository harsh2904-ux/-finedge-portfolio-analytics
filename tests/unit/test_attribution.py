"""
Unit Tests for Beta and Risk Attribution Engine
"""

import numpy as np
import pandas as pd
from portfolio_analytics.analytics.beta import BetaCalculator
from portfolio_analytics.analytics.attribution import RiskAttributionCalculator


def test_beta_calculation():
    # Benchmark returns
    np.random.seed(42)
    bench_ret = pd.Series(np.random.normal(0.0005, 0.01, 100))
    # Portfolio return is 1.5 * bench + noise
    port_ret = 1.5 * bench_ret + np.random.normal(0.0, 0.002, 100)

    beta, alpha = BetaCalculator.calculate_beta_and_alpha(port_ret, bench_ret)
    assert beta is not None
    assert np.isclose(beta, 1.5, atol=0.1)


def test_euler_risk_attribution_sums_to_100_percent(sample_price_matrix):
    weights = {"AAPL": 0.50, "BND": 0.30, "NVDA": 0.20}
    res = RiskAttributionCalculator.calculate_risk_attribution(sample_price_matrix, weights)

    assert res.portfolio_volatility > 0
    pcr_sum = sum(res.percentage_risk_contribution.values())
    # Percentage contributions must sum to 100%
    assert np.isclose(pcr_sum, 100.0, atol=0.1)
