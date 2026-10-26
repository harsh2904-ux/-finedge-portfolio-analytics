"""
Unit Tests for Performance Metrics
"""

import numpy as np
import pandas as pd
from datetime import datetime, timedelta
from portfolio_analytics.analytics.performance import PerformanceCalculator


def test_total_return():
    prices = pd.Series([100.0, 150.0])
    tot_ret = PerformanceCalculator.calculate_total_return(prices)
    assert np.isclose(tot_ret, 0.50)


def test_cagr():
    # 252 days with 10% gain -> 1 year CAGR = 10%
    dates = [datetime(2024, 1, 1) + timedelta(days=i) for i in range(252)]
    prices = pd.Series([100.0] * 251 + [110.0], index=dates)
    cagr = PerformanceCalculator.calculate_cagr(prices, trading_days=252)
    assert np.isclose(cagr, 0.10, atol=1e-4)


def test_benchmark_performance_analysis():
    dates = [datetime(2024, 1, 1) + timedelta(days=i) for i in range(50)]
    port_prices = pd.Series(np.linspace(100, 120, 50), index=dates)
    bench_prices = pd.Series(np.linspace(100, 110, 50), index=dates)

    metrics = PerformanceCalculator.analyze_performance(port_prices, bench_prices, trading_days=252)
    assert np.isclose(metrics.total_return, 0.20, atol=1e-3)
    assert np.isclose(metrics.benchmark_return, 0.10, atol=1e-3)
    assert np.isclose(metrics.excess_return, 0.10, atol=1e-3)
