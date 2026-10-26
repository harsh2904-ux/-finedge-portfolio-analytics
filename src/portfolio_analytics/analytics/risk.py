"""
Quantitative Risk Analytics Engine: Volatility, Sharpe, Sortino, VaR, and Expected Shortfall
"""

from typing import Optional
import numpy as np
import pandas as pd
try:
    from scipy import stats
    def _norm_ppf(p: float) -> float:
        return float(stats.norm.ppf(p))
except Exception:
    from statistics import NormalDist
    def _norm_ppf(p: float) -> float:
        return float(NormalDist().inv_cdf(p))

from portfolio_analytics.analytics.models import RiskMetrics
from portfolio_analytics.analytics.drawdown import DrawdownCalculator


class RiskCalculator:
    """
    Computes statistical and probabilistic portfolio risk metrics.
    """

    @staticmethod
    def calculate_daily_volatility(daily_returns: pd.Series) -> float:
        """Sample standard deviation of periodic daily returns."""
        if daily_returns.empty or len(daily_returns) < 2:
            return 0.0
        return float(daily_returns.std(ddof=1))

    @staticmethod
    def calculate_annualized_volatility(
        daily_returns: pd.Series,
        trading_days: int = 252,
    ) -> float:
        """
        Annualized Volatility = Daily Volatility * sqrt(trading_days)
        """
        daily_vol = RiskCalculator.calculate_daily_volatility(daily_returns)
        return float(daily_vol * np.sqrt(trading_days))

    @staticmethod
    def calculate_sharpe_ratio(
        annualized_return: float,
        annualized_volatility: float,
        risk_free_rate: float = 0.04,
    ) -> float:
        """
        Sharpe Ratio = (Annualized Return - Risk Free Rate) / Annualized Volatility
        """
        if annualized_volatility <= 0:
            return 0.0
        return float((annualized_return - risk_free_rate) / annualized_volatility)

    @staticmethod
    def calculate_downside_deviation(
        daily_returns: pd.Series,
        target_return: float = 0.0,
        trading_days: int = 252,
    ) -> float:
        """
        Annualized Downside Deviation (Semi-deviation):
        sqrt( 1/N * Sum( min(0, R_t - target)^2 ) ) * sqrt(trading_days)
        """
        if daily_returns.empty or len(daily_returns) < 2:
            return 0.0

        daily_target = target_return / trading_days
        downside_diff = np.minimum(0.0, daily_returns - daily_target)
        downside_variance = np.mean(downside_diff ** 2)
        daily_downside_dev = np.sqrt(downside_variance)
        return float(daily_downside_dev * np.sqrt(trading_days))

    @staticmethod
    def calculate_sortino_ratio(
        annualized_return: float,
        daily_returns: pd.Series,
        risk_free_rate: float = 0.04,
        trading_days: int = 252,
    ) -> float:
        """
        Sortino Ratio = (Annualized Return - Risk Free Rate) / Downside Deviation
        """
        downside_dev = RiskCalculator.calculate_downside_deviation(
            daily_returns=daily_returns,
            target_return=risk_free_rate,
            trading_days=trading_days,
        )
        if downside_dev <= 0:
            return 0.0
        return float((annualized_return - risk_free_rate) / downside_dev)

    @staticmethod
    def calculate_historical_var(
        daily_returns: pd.Series,
        confidence_level: float = 0.95,
    ) -> float:
        """
        Historical Value at Risk (1-day horizon percentage).
        Reported as a positive loss percentage.
        """
        if daily_returns.empty:
            return 0.0
        # (1 - confidence_level) quantile
        cutoff_quantile = 1.0 - confidence_level
        var_cutoff = float(np.percentile(daily_returns, cutoff_quantile * 100.0))
        # Positive loss representation
        return float(-var_cutoff if var_cutoff < 0 else 0.0)

    @staticmethod
    def calculate_parametric_var(
        daily_returns: pd.Series,
        confidence_level: float = 0.95,
    ) -> float:
        """
        Parametric Gaussian Value at Risk:
        VaR = - (mu - Z * sigma)
        """
        if daily_returns.empty or len(daily_returns) < 2:
            return 0.0
        mu = daily_returns.mean()
        sigma = daily_returns.std(ddof=1)
        z = _norm_ppf(confidence_level)
        var_cutoff = mu - (z * sigma)
        return float(-var_cutoff if var_cutoff < 0 else 0.0)

    @staticmethod
    def calculate_expected_shortfall(
        daily_returns: pd.Series,
        confidence_level: float = 0.95,
    ) -> float:
        """
        Conditional Value at Risk (CVaR) / Expected Shortfall:
        Average loss strictly beyond the historical VaR threshold.
        """
        if daily_returns.empty:
            return 0.0
        cutoff_quantile = 1.0 - confidence_level
        threshold = np.percentile(daily_returns, cutoff_quantile * 100.0)
        tail_losses = daily_returns[daily_returns <= threshold]
        if tail_losses.empty:
            return float(-threshold if threshold < 0 else 0.0)
        return float(-tail_losses.mean())

    @classmethod
    def analyze_risk(
        cls,
        portfolio_prices: pd.Series,
        current_portfolio_value: float,
        annualized_return: float,
        beta: Optional[float] = None,
        alpha: Optional[float] = None,
        risk_free_rate: float = 0.04,
        trading_days: int = 252,
    ) -> RiskMetrics:
        """
        Comprehensive risk suite assembling volatility, Sharpe, Sortino, drawdowns, VaR, and ES.
        """
        daily_returns = portfolio_prices.pct_change().dropna()

        daily_vol = cls.calculate_daily_volatility(daily_returns)
        ann_vol = cls.calculate_annualized_volatility(daily_returns, trading_days=trading_days)
        sharpe = cls.calculate_sharpe_ratio(annualized_return, ann_vol, risk_free_rate)
        sortino = cls.calculate_sortino_ratio(annualized_return, daily_returns, risk_free_rate, trading_days)

        dd_result = DrawdownCalculator.calculate_max_drawdown(portfolio_prices)

        var_hist_95 = cls.calculate_historical_var(daily_returns, 0.95)
        var_hist_99 = cls.calculate_historical_var(daily_returns, 0.99)
        var_param_95 = cls.calculate_parametric_var(daily_returns, 0.95)
        var_param_99 = cls.calculate_parametric_var(daily_returns, 0.99)

        es_95 = cls.calculate_expected_shortfall(daily_returns, 0.95)
        es_96_99 = cls.calculate_expected_shortfall(daily_returns, 0.99)

        var_monetary_95 = current_portfolio_value * var_hist_95
        var_monetary_99 = current_portfolio_value * var_hist_99

        return RiskMetrics(
            daily_volatility=round(daily_vol, 6),
            annualized_volatility=round(ann_vol, 6),
            sharpe_ratio=round(sharpe, 4),
            sortino_ratio=round(sortino, 4),
            max_drawdown=round(dd_result.max_drawdown, 6),
            peak_date=dd_result.peak_date,
            trough_date=dd_result.trough_date,
            recovery_date=dd_result.recovery_date,
            recovery_duration_days=dd_result.recovery_duration_days,
            var_historical_95=round(var_hist_95, 6),
            var_historical_99=round(var_hist_99, 6),
            var_parametric_95=round(var_param_95, 6),
            var_parametric_99=round(var_param_99, 6),
            var_monetary_95=round(var_monetary_95, 2),
            var_monetary_99=round(var_monetary_99, 2),
            expected_shortfall_95=round(es_95, 6),
            expected_shortfall_99=round(es_96_99, 6),
            beta=round(beta, 4) if beta is not None else None,
            alpha=round(alpha, 6) if alpha is not None else None,
            risk_free_rate=risk_free_rate,
            trading_days=trading_days,
        )
