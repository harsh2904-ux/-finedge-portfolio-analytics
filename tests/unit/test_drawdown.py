"""
Unit Tests for Drawdown and Recovery Engine
"""

import numpy as np
import pandas as pd
from datetime import datetime, timedelta
from portfolio_analytics.analytics.drawdown import DrawdownCalculator


def test_drawdown_calculation_with_recovery():
    dates = [datetime(2024, 1, 1) + timedelta(days=i) for i in range(5)]
    # 100 -> 120 (Peak) -> 90 (Trough: -25% drop) -> 110 -> 125 (Recovered!)
    prices = pd.Series([100.0, 120.0, 90.0, 110.0, 125.0], index=dates)

    res = DrawdownCalculator.calculate_max_drawdown(prices)
    assert np.isclose(res.max_drawdown, -0.25)
    assert res.peak_date == "2024-01-02"
    assert res.trough_date == "2024-01-03"
    assert res.is_recovered is True
    assert res.recovery_date == "2024-01-05"
    assert res.recovery_duration_days == 3


def test_zero_drawdown_on_monotonic_growth():
    prices = pd.Series([100.0, 105.0, 110.0, 115.0])
    res = DrawdownCalculator.calculate_max_drawdown(prices)
    assert np.isclose(res.max_drawdown, 0.0)
    assert res.is_recovered is True
