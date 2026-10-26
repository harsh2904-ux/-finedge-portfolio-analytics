"""
Unit Tests for Return Calculations
"""

import numpy as np
import pandas as pd
from portfolio_analytics.analytics.returns import ReturnCalculator


def test_simple_returns():
    prices = pd.Series([100.0, 105.0, 102.0])
    returns = ReturnCalculator.calculate_simple_returns(prices)
    assert len(returns) == 2
    assert np.isclose(returns.iloc[0], 0.05)
    assert np.isclose(returns.iloc[1], (102.0 - 105.0) / 105.0)


def test_log_returns():
    prices = pd.Series([100.0, 110.0])
    log_ret = ReturnCalculator.calculate_log_returns(prices)
    assert np.isclose(log_ret.iloc[0], np.log(1.10))


def test_cumulative_returns():
    prices = pd.Series([100.0, 110.0, 125.0])
    cum_ret = ReturnCalculator.calculate_cumulative_returns(prices)
    assert np.isclose(cum_ret.iloc[0], 0.0)
    assert np.isclose(cum_ret.iloc[1], 0.10)
    assert np.isclose(cum_ret.iloc[2], 0.25)


def test_rolling_returns():
    prices = pd.Series([100.0, 102.0, 105.0, 110.0])
    roll = ReturnCalculator.calculate_rolling_returns(prices, window=2)
    assert np.isnan(roll.iloc[0])
    assert np.isnan(roll.iloc[1])
    assert np.isclose(roll.iloc[2], (105.0 / 100.0) - 1.0)
    assert np.isclose(roll.iloc[3], (110.0 / 102.0) - 1.0)
