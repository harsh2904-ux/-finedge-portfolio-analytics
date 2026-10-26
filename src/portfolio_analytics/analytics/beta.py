"""
Beta and Jensen's Alpha Engine: Covariance/Variance and OLS Regression against Benchmark
"""

from typing import Optional, Tuple
import numpy as np
import pandas as pd


class BetaCalculator:
    """
    Computes portfolio and asset-level Beta and Jensen's Alpha.
    """

    @staticmethod
    def calculate_beta_and_alpha(
        portfolio_returns: pd.Series,
        benchmark_returns: pd.Series,
        risk_free_rate: float = 0.04,
        trading_days: int = 252,
    ) -> Tuple[Optional[float], Optional[float]]:
        """
        Calculates:
        Beta = Cov(R_p, R_b) / Var(R_b)
        Jensen's Alpha = Annualized R_p - (R_f + Beta * (Annualized R_b - R_f))
        """
        if benchmark_returns is None or benchmark_returns.empty:
            return None, None

        aligned = pd.concat([portfolio_returns, benchmark_returns], axis=1, join="inner").dropna()
        if len(aligned) < 10 or aligned.shape[1] < 2:
            return None, None

        p_ret = aligned.iloc[:, 0]
        b_ret = aligned.iloc[:, 1]

        b_var = b_ret.var(ddof=1)
        if b_var <= 0:
            return None, None

        cov_pb = p_ret.cov(b_ret)
        beta = float(cov_pb / b_var)

        # Annualize returns for Jensen's Alpha
        p_ann = float((1.0 + p_ret.mean()) ** trading_days - 1.0)
        b_ann = float((1.0 + b_ret.mean()) ** trading_days - 1.0)

        alpha = float(p_ann - (risk_free_rate + beta * (b_ann - risk_free_rate)))
        return round(beta, 4), round(alpha, 6)
