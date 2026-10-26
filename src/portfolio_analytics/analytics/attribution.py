"""
Euler Risk Attribution Engine: Marginal, Component, and Percentage Contribution to Risk
"""

from typing import Dict, List
import numpy as np
import pandas as pd
from portfolio_analytics.analytics.models import RiskAttributionResult


class RiskAttributionCalculator:
    """
    Computes Euler decomposition of portfolio risk into individual asset contributions.
    """

    @staticmethod
    def calculate_risk_attribution(
        price_matrix: pd.DataFrame,
        weights: Dict[str, float],
    ) -> RiskAttributionResult:
        """
        Decomposes portfolio volatility using Euler's theorem:
        sigma_p = sqrt(w^T * Sigma * w)
        MCR = (Sigma * w) / sigma_p
        CCR = w * MCR
        PCR = CCR / sigma_p
        """
        # Align weights with columns
        active_assets = [s for s in price_matrix.columns if s in weights and weights[s] > 0]
        if len(active_assets) < 2:
            return RiskAttributionResult(
                portfolio_volatility=0.0,
                marginal_contribution_to_risk={},
                component_contribution_to_risk={},
                percentage_risk_contribution={},
            )

        returns_df = price_matrix[active_assets].pct_change().dropna()
        # Annualized covariance matrix
        cov_matrix = (returns_df.cov() * 252.0).values
        w = np.array([weights[s] for s in active_assets])
        # Normalize weights to sum to 1.0 for risk attribution
        w = w / np.sum(w)

        # Portfolio variance and volatility
        port_var = float(np.dot(w.T, np.dot(cov_matrix, w)))
        port_vol = float(np.sqrt(port_var)) if port_var > 0 else 0.0001

        # Marginal Contribution to Risk (MCR)
        mcr = np.dot(cov_matrix, w) / port_vol

        # Component Contribution to Risk (CCR)
        ccr = w * mcr

        # Percentage Contribution to Risk (PCR)
        pcr = (ccr / port_vol) * 100.0

        return RiskAttributionResult(
            portfolio_volatility=round(port_vol, 6),
            marginal_contribution_to_risk={s: round(float(mcr[i]), 6) for i, s in enumerate(active_assets)},
            component_contribution_to_risk={s: round(float(ccr[i]), 6) for i, s in enumerate(active_assets)},
            percentage_risk_contribution={s: round(float(pcr[i]), 2) for i, s in enumerate(active_assets)},
        )
