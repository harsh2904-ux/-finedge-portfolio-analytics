"""
Return Calculations Engine: Simple, Log, Periodic, Cumulative, and Rolling Returns
"""

from typing import Union
import numpy as np
import pandas as pd


class ReturnCalculator:
    """
    Computes mathematically rigorous returns for series and matrices.
    """

    @staticmethod
    def calculate_simple_returns(prices: Union[pd.Series, pd.DataFrame]) -> Union[pd.Series, pd.DataFrame]:
        """
        Computes discrete simple returns:
        R_t = (P_t / P_{t-1}) - 1
        """
        return prices.pct_change().dropna()

    @staticmethod
    def calculate_log_returns(prices: Union[pd.Series, pd.DataFrame]) -> Union[pd.Series, pd.DataFrame]:
        """
        Computes continuously compounded log returns:
        r_t = ln(P_t / P_{t-1})
        """
        return np.log(prices / prices.shift(1)).dropna()

    @staticmethod
    def calculate_cumulative_returns(prices: Union[pd.Series, pd.DataFrame]) -> Union[pd.Series, pd.DataFrame]:
        """
        Computes cumulative returns from beginning of series:
        CR_t = (P_t / P_0) - 1
        """
        if isinstance(prices, pd.Series):
            base_price = prices.iloc[0]
            return (prices / base_price) - 1.0
        else:
            base_prices = prices.iloc[0]
            return (prices.divide(base_prices, axis=1)) - 1.0

    @staticmethod
    def calculate_periodic_returns(
        prices: Union[pd.Series, pd.DataFrame],
        freq: str = "M",
    ) -> Union[pd.Series, pd.DataFrame]:
        """
        Resamples prices to specified frequency and computes simple returns.
        Supported freq: 'W' (Weekly), 'M' (Monthly), 'Y' (Annual).
        """
        # Pandas 2.2+ frequency aliases
        alias_map = {"W": "W-FRI", "M": "ME", "Y": "YE"}
        resample_rule = alias_map.get(freq.upper(), freq)
        periodic_prices = prices.resample(resample_rule).last()
        return periodic_prices.pct_change().dropna()

    @staticmethod
    def calculate_rolling_returns(
        prices: Union[pd.Series, pd.DataFrame],
        window: int = 30,
    ) -> Union[pd.Series, pd.DataFrame]:
        """
        Computes rolling k-day returns:
        RR_t = (P_t / P_{t-k}) - 1
        """
        return (prices / prices.shift(window)) - 1.0
