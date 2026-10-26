"""
Monte Carlo Simulation Engine for Portfolio Trajectory Forecasting
"""

from typing import Dict, List, Optional
import numpy as np
import pandas as pd
from portfolio_analytics.optimization.models import MonteCarloResult


class MonteCarloEngine:
    """
    Simulates multi-asset stochastic portfolio paths using Geometric Brownian Motion
    and Cholesky decomposition to preserve asset correlation structure.
    """

    @staticmethod
    def simulate(
        price_matrix: pd.DataFrame,
        weights: Dict[str, float],
        initial_value: float = 100000.0,
        time_horizon_days: int = 252,
        num_simulations: int = 10000,
        random_seed: Optional[int] = 42,
    ) -> MonteCarloResult:
        if random_seed is not None:
            np.random.seed(random_seed)

        # Align assets
        active_assets = [s for s in price_matrix.columns if s in weights and weights[s] > 0]
        if not active_assets:
            return MonteCarloResult(
                num_simulations=num_simulations,
                time_horizon_days=time_horizon_days,
                initial_value=initial_value,
                mean_ending_value=initial_value,
                median_ending_value=initial_value,
                std_ending_value=0.0,
                p5_ending_value=initial_value,
                p25_ending_value=initial_value,
                p75_ending_value=initial_value,
                p95_ending_value=initial_value,
                probability_of_loss=0.0,
                simulation_paths_sample=[],
            )

        returns_df = price_matrix[active_assets].pct_change().dropna()
        num_assets = len(active_assets)
        w = np.array([weights[s] for s in active_assets])
        w = w / np.sum(w)  # Normalize

        # Daily mean and covariance
        mean_daily_ret = returns_df.mean().to_numpy(copy=True)
        cov_daily = returns_df.cov().to_numpy(copy=True)

        # Cholesky decomposition of covariance matrix for correlated random shocks
        # Add small epsilon to diagonal if necessary for positive definiteness
        try:
            L = np.linalg.cholesky(cov_daily)
        except np.linalg.LinAlgError:
            cov_daily = cov_daily + np.eye(num_assets) * 1e-6
            L = np.linalg.cholesky(cov_daily)

        # Generate standard normal random variables: shape (num_simulations, time_horizon_days, num_assets)
        Z = np.random.normal(size=(num_simulations, time_horizon_days, num_assets))

        # Correlated shocks: shape (num_simulations, time_horizon_days, num_assets)
        # correlated_shocks[s, t] = Z[s, t] @ L.T
        correlated_shocks = np.matmul(Z, L.T)

        # Asset returns with drift: R = mu + correlated_shocks
        sim_asset_returns = mean_daily_ret + correlated_shocks

        # Portfolio return at each step: dot product with weights -> shape (num_simulations, time_horizon_days)
        sim_port_returns = np.dot(sim_asset_returns, w)

        # Cumulative price trajectories: shape (num_simulations, time_horizon_days + 1)
        # V_t = V_0 * cumprod(1 + R_t)
        cumulative_factors = np.cumprod(1.0 + sim_port_returns, axis=1)

        # Prepend initial 1.0
        full_paths = np.ones((num_simulations, time_horizon_days + 1))
        full_paths[:, 1:] = cumulative_factors
        full_paths *= initial_value

        ending_values = full_paths[:, -1]

        mean_ending = float(np.mean(ending_values))
        median_ending = float(np.median(ending_values))
        std_ending = float(np.std(ending_values))

        p5 = float(np.percentile(ending_values, 5))
        p25 = float(np.percentile(ending_values, 25))
        p75 = float(np.percentile(ending_values, 75))
        p95 = float(np.percentile(ending_values, 95))

        prob_loss = float(np.mean(ending_values < initial_value))

        # Downsample 15 representative paths for later frontend visualization
        sample_step = max(1, num_simulations // 15)
        path_samples = full_paths[::sample_step][:15].round(2).tolist()

        return MonteCarloResult(
            num_simulations=num_simulations,
            time_horizon_days=time_horizon_days,
            initial_value=round(initial_value, 2),
            mean_ending_value=round(mean_ending, 2),
            median_ending_value=round(median_ending, 2),
            std_ending_value=round(std_ending, 2),
            p5_ending_value=round(p5, 2),
            p25_ending_value=round(p25, 2),
            p75_ending_value=round(p75, 2),
            p95_ending_value=round(p95, 2),
            probability_of_loss=round(prob_loss, 4),
            simulation_paths_sample=path_samples,
        )
