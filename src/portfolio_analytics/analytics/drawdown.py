"""
Drawdown Engine: Peak-to-Trough Maximum Drawdown, Dates, and Recovery Durations
"""

from typing import Tuple
import pandas as pd
from portfolio_analytics.analytics.models import DrawdownResult


class DrawdownCalculator:
    """
    Computes running peak, drawdown series, maximum drawdown, and recovery metrics.
    """

    @staticmethod
    def calculate_drawdown_series(prices: pd.Series) -> pd.Series:
        """
        Computes the percentage drawdown at each point in time:
        DD_t = (P_t - Peak_t) / Peak_t
        """
        if prices.empty:
            return pd.Series(dtype=float)
        running_peak = prices.cummax()
        drawdown = (prices - running_peak) / running_peak
        return drawdown

    @classmethod
    def calculate_max_drawdown(cls, prices: pd.Series) -> DrawdownResult:
        """
        Identifies Maximum Drawdown, Peak Date, Trough Date, and Recovery details.
        """
        if prices.empty or len(prices) < 2:
            return DrawdownResult(max_drawdown=0.0, is_recovered=True)

        clean_prices = prices.dropna()
        running_peak = clean_prices.cummax()
        drawdown = (clean_prices - running_peak) / running_peak

        # Identify trough (lowest drawdown point)
        trough_idx = drawdown.idxmin()
        max_dd = float(drawdown.loc[trough_idx])

        if max_dd >= 0.0:
            return DrawdownResult(max_drawdown=0.0, is_recovered=True)

        # Identify the peak preceding the trough
        prices_before_trough = clean_prices.loc[:trough_idx]
        peak_idx = prices_before_trough.idxmax()
        peak_val = float(clean_prices.loc[peak_idx])

        # Check for recovery after trough
        prices_after_trough = clean_prices.loc[trough_idx:]
        recovery_slice = prices_after_trough[prices_after_trough >= peak_val]

        recovery_date = None
        recovery_days = None
        is_recovered = False

        if not recovery_slice.empty:
            rec_idx = recovery_slice.index[0]
            is_recovered = True
            recovery_date = str(rec_idx.date()) if hasattr(rec_idx, "date") else str(rec_idx)
            try:
                recovery_days = (pd.to_datetime(rec_idx) - pd.to_datetime(peak_idx)).days
            except Exception:
                recovery_days = None

        peak_str = str(peak_idx.date()) if hasattr(peak_idx, "date") else str(peak_idx)
        trough_str = str(trough_idx.date()) if hasattr(trough_idx, "date") else str(trough_idx)

        return DrawdownResult(
            max_drawdown=round(max_dd, 6),
            peak_date=peak_str,
            trough_date=trough_str,
            recovery_date=recovery_date,
            recovery_duration_days=recovery_days,
            is_recovered=is_recovered,
        )
