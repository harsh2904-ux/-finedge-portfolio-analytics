"""
Correlation and Covariance Analytics Engine
"""

from typing import Dict, List, Optional
import pandas as pd
from portfolio_analytics.analytics.models import CorrelationResult


class CorrelationCalculator:
    """
    Computes Pearson Correlation and Covariance matrices across assets.
    """

    LOOKBACK_DAYS = {
        "30D": 30,
        "90D": 90,
        "1Y": 252,
        "3Y": 756,
        "ALL": None,
    }

    @classmethod
    def analyze_correlation(
        cls,
        price_matrix: pd.DataFrame,
        lookback: str = "1Y",
    ) -> CorrelationResult:
        """
        Calculates correlation and covariance matrices for asset returns.
        """
        if price_matrix.empty or len(price_matrix.columns) < 2:
            return CorrelationResult(
                assets=list(price_matrix.columns),
                correlation_matrix={},
                covariance_matrix={},
                lookback_period=lookback,
            )

        returns_df = price_matrix.pct_change().dropna()

        # Slice lookback
        days = cls.LOOKBACK_DAYS.get(lookback.upper())
        if days and len(returns_df) > days:
            returns_df = returns_df.iloc[-days:]

        corr_df = returns_df.corr(method="pearson").round(4)
        cov_df = (returns_df.cov() * 252.0).round(6)  # Annualized covariance

        return CorrelationResult(
            assets=list(corr_df.columns),
            correlation_matrix=corr_df.to_dict(),
            covariance_matrix=cov_df.to_dict(),
            lookback_period=lookback,
        )
