"""
Unit Tests for Quantitative Risk Metrics
"""

import numpy as np
import pandas as pd
from portfolio_analytics.analytics.risk import RiskCalculator


def test_volatility_annualization():
    returns = pd.Series([0.01, -0.01, 0.02, -0.02, 0.005])
    daily_vol = RiskCalculator.calculate_daily_volatility(returns)
    ann_vol = RiskCalculator.calculate_annualized_volatility(returns, trading_days=252)
    assert np.isclose(ann_vol, daily_vol * np.sqrt(252))


def test_sharpe_ratio():
    ann_ret = 0.12
    ann_vol = 0.10
    rf = 0.04
    sharpe = RiskCalculator.calculate_sharpe_ratio(ann_ret, ann_vol, rf)
    assert np.isclose(sharpe, (0.12 - 0.04) / 0.10)


def test_sortino_ratio_ignores_upside():
    # Only positive returns: downside deviation is 0, Sortino is 0 or safe
    positive_returns = pd.Series([0.02, 0.03, 0.01, 0.04])
    downside_dev = RiskCalculator.calculate_downside_deviation(positive_returns, target_return=0.0)
    assert np.isclose(downside_dev, 0.0)


def test_historical_and_parametric_var():
    np.random.seed(42)
    # 1000 standard normal returns with mean 0, std 0.01
    norm_returns = pd.Series(np.random.normal(0.0, 0.01, 1000))

    hist_var_95 = RiskCalculator.calculate_historical_var(norm_returns, 0.95)
    param_var_95 = RiskCalculator.calculate_parametric_var(norm_returns, 0.95)

    # Both should be approx 1.645 * 0.01 = 0.01645
    assert np.isclose(hist_var_95, 0.01645, atol=0.003)
    assert np.isclose(param_var_95, 0.01645, atol=0.002)


def test_expected_shortfall():
    # Tail losses beyond VaR should have higher average loss than VaR itself
    np.random.seed(42)
    returns = pd.Series(np.random.normal(0.0, 0.02, 2000))
    var_95 = RiskCalculator.calculate_historical_var(returns, 0.95)
    es_95 = RiskCalculator.calculate_expected_shortfall(returns, 0.95)
    assert es_95 >= var_95
