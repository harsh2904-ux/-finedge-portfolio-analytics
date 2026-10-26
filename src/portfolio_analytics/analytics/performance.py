"""
Performance Analytics Engine: Total Return, CAGR, Annualized Return, Benchmark Comparison
"""

from typing import Optional, Tuple
import numpy as np
import pandas as pd
from portfolio_analytics.analytics.models import PerformanceMetrics


class PerformanceCalculator:
    """
    Computes portfolio performance indicators and benchmark tracking.
    """

    @staticmethod
    def calculate_total_return(price_series: pd.Series) -> float:
        """
        Total Return: P_end / P_start - 1
        """
        if price_series.empty or len(price_series) < 2:
            return 0.0
        start_val = float(price_series.iloc[0])
        end_val = float(price_series.iloc[-1])
        if start_val <= 0:
            return 0.0
        return (end_val / start_val) - 1.0

    @staticmethod
    def calculate_cagr(price_series: pd.Series, trading_days: int = 252) -> float:
        """
        Compound Annual Growth Rate (CAGR):
        (Ending / Beginning)^(1 / Years) - 1
        """
        if price_series.empty or len(price_series) < 2:
            return 0.0
        start_val = float(price_series.iloc[0])
        end_val = float(price_series.iloc[-1])
        if start_val <= 0 or end_val <= 0:
            return 0.0

        n_days = len(price_series)
        years = n_days / float(trading_days)
        if years <= 0:
            return 0.0

        return float((end_val / start_val) ** (1.0 / years) - 1.0)

    @staticmethod
    def calculate_annualized_return(
        daily_returns: pd.Series,
        trading_days: int = 252,
    ) -> float:
        """
        Geometric compounding annualized return:
        (Prod(1 + R_t))^(252 / N) - 1
        """
        if daily_returns.empty:
            return 0.0
        n = len(daily_returns)
        cumulative_growth = np.prod(1.0 + daily_returns)
        if cumulative_growth <= 0 or n == 0:
            return 0.0
        return float(cumulative_growth ** (trading_days / n) - 1.0)

    @classmethod
    def analyze_performance(
        cls,
        portfolio_series: pd.Series,
        benchmark_series: Optional[pd.Series] = None,
        trading_days: int = 252,
    ) -> PerformanceMetrics:
        """
        Full performance suite comparing portfolio to optional benchmark.
        """
        port_clean = portfolio_series.dropna()
        tot_ret = cls.calculate_total_return(port_clean)
        cagr = cls.calculate_cagr(port_clean, trading_days=trading_days)

        port_daily_ret = port_clean.pct_change().dropna()
        ann_ret = cls.calculate_annualized_return(port_daily_ret, trading_days=trading_days)

        bench_ret = None
        excess_ret = None
        track_diff = None
        track_error = None
        info_ratio = None

        if benchmark_series is not None and not benchmark_series.empty:
            bench_clean = benchmark_series.dropna()
            # Align dates
            aligned = pd.concat([port_clean, bench_clean], axis=1, join="inner").dropna()
            if len(aligned) >= 2:
                port_aligned = aligned.iloc[:, 0]
                bench_aligned = aligned.iloc[:, 1]

                b_tot_ret = cls.calculate_total_return(bench_aligned)
                p_tot_ret = cls.calculate_total_return(port_aligned)
                bench_ret = b_tot_ret
                excess_ret = p_tot_ret - b_tot_ret
                track_diff = excess_ret

                # Daily return excess
                p_ret = port_aligned.pct_change().dropna()
                b_ret = bench_aligned.pct_change().dropna()
                excess_daily = p_ret - b_ret

                if len(excess_daily) > 1:
                    track_error = float(excess_daily.std() * np.sqrt(trading_days))
                    if track_error > 0:
                        info_ratio = excess_ret / track_error

        return PerformanceMetrics(
            total_return=round(tot_ret, 6),
            cagr=round(cagr, 6),
            annualized_return=round(ann_ret, 6),
            benchmark_return=round(bench_ret, 6) if bench_ret is not None else None,
            excess_return=round(excess_ret, 6) if excess_ret is not None else None,
            tracking_difference=round(track_diff, 6) if track_diff is not None else None,
            tracking_error=round(track_error, 6) if track_error is not None else None,
            information_ratio=round(info_ratio, 4) if info_ratio is not None else None,
        )
