"""
Comprehensive Portfolio Analytics Pipeline
"""

from datetime import datetime, timezone
from typing import Dict, List, Optional
import pandas as pd
from pydantic import BaseModel
from sqlalchemy.orm import Session
from portfolio_analytics.portfolio.portfolio_service import PortfolioService
from portfolio_analytics.portfolio.valuation import PortfolioSnapshot
from portfolio_analytics.analytics.models import (
    PerformanceMetrics,
    RiskMetrics,
    CorrelationResult,
    RiskAttributionResult,
)
from portfolio_analytics.analytics.performance import PerformanceCalculator
from portfolio_analytics.analytics.risk import RiskCalculator
from portfolio_analytics.analytics.beta import BetaCalculator
from portfolio_analytics.analytics.correlation import CorrelationCalculator
from portfolio_analytics.analytics.attribution import RiskAttributionCalculator
from portfolio_analytics.optimization.models import (
    OptimizationResult,
    EfficientFrontierResult,
    MonteCarloResult,
)
from portfolio_analytics.optimization.minimum_variance import MinimumVarianceOptimizer
from portfolio_analytics.optimization.maximum_sharpe import MaximumSharpeOptimizer
from portfolio_analytics.optimization.efficient_frontier import EfficientFrontierEngine
from portfolio_analytics.optimization.monte_carlo import MonteCarloEngine
from portfolio_analytics.scenarios.scenario_engine import ScenarioEngine, ScenarioResult
from portfolio_analytics.config.settings import settings
from portfolio_analytics.config.logging_config import logger


class PortfolioComprehensiveReport(BaseModel):
    """Unified, JSON-serializable analytical report for Spring Boot and frontend consumption."""
    generated_at: datetime
    snapshot: PortfolioSnapshot
    performance: PerformanceMetrics
    risk: RiskMetrics
    correlation: CorrelationResult
    risk_attribution: RiskAttributionResult
    optimization: Dict[str, OptimizationResult]
    efficient_frontier: EfficientFrontierResult
    monte_carlo: MonteCarloResult
    stress_tests: List[ScenarioResult]


class AnalyticsPipeline:
    """
    Coordinates end-to-end quantitative analytics computation for a portfolio.
    """

    def __init__(self, session: Session):
        self.session = session
        self.portfolio_service = PortfolioService(session)

    def run_portfolio_analytics(
        self,
        portfolio_id: int,
        start_date: Optional[datetime] = None,
        end_date: Optional[datetime] = None,
        risk_free_rate: Optional[float] = None,
        trading_days: Optional[int] = None,
        mc_simulations: int = 5000,
        ef_simulations: int = 2000,
    ) -> PortfolioComprehensiveReport:
        """
        Executes full quantitative analytics suite for the specified portfolio.
        """
        rf = risk_free_rate if risk_free_rate is not None else settings.DEFAULT_RISK_FREE_RATE
        td = trading_days if trading_days is not None else settings.TRADING_DAYS_PER_YEAR

        logger.info(f"Running comprehensive analytics for portfolio ID={portfolio_id}")

        # 1. Valuation Snapshot
        snapshot = self.portfolio_service.get_current_snapshot(portfolio_id)

        # 2. Historical Series
        port_series, bench_series, price_matrix = self.portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=portfolio_id,
            start_date=start_date,
            end_date=end_date,
        )

        if port_series.empty or len(port_series) < 5:
            raise ValueError(f"Insufficient historical price data to analyze portfolio ID={portfolio_id}.")

        # 3. Performance Metrics
        performance = PerformanceCalculator.analyze_performance(
            portfolio_series=port_series,
            benchmark_series=bench_series,
            trading_days=td,
        )

        # 4. Beta & Alpha
        port_daily_ret = port_series.pct_change().dropna()
        bench_daily_ret = bench_series.pct_change().dropna() if not bench_series.empty else pd.Series(dtype=float)
        beta, alpha = BetaCalculator.calculate_beta_and_alpha(
            portfolio_returns=port_daily_ret,
            benchmark_returns=bench_daily_ret,
            risk_free_rate=rf,
            trading_days=td,
        )

        # 5. Risk Metrics
        risk = RiskCalculator.analyze_risk(
            portfolio_prices=port_series,
            current_portfolio_value=snapshot.total_market_value,
            annualized_return=performance.annualized_return,
            beta=beta,
            alpha=alpha,
            risk_free_rate=rf,
            trading_days=td,
        )

        # 6. Correlation & Covariance Matrix
        # Filter price matrix to only include portfolio assets (excluding standalone benchmark if not held)
        held_symbols = [p.symbol for p in snapshot.positions]
        held_price_matrix = price_matrix[[s for s in held_symbols if s in price_matrix.columns]]

        correlation = CorrelationCalculator.analyze_correlation(held_price_matrix, lookback="1Y")

        # 7. Risk Attribution (Euler Decomposition)
        weights_dict = {p.symbol: p.actual_weight for p in snapshot.positions}
        attribution = RiskAttributionCalculator.calculate_risk_attribution(held_price_matrix, weights_dict)

        # 8. Portfolio Optimization (Min Volatility & Max Sharpe)
        min_vol = MinimumVarianceOptimizer.optimize(held_price_matrix, risk_free_rate=rf)
        max_sharpe = MaximumSharpeOptimizer.optimize(held_price_matrix, risk_free_rate=rf)

        # 9. Efficient Frontier
        frontier = EfficientFrontierEngine.generate_frontier(
            price_matrix=held_price_matrix,
            num_portfolios=ef_simulations,
            risk_free_rate=rf,
        )

        # 10. Monte Carlo Simulation
        monte_carlo = MonteCarloEngine.simulate(
            price_matrix=held_price_matrix,
            weights=weights_dict,
            initial_value=snapshot.total_market_value,
            time_horizon_days=252,
            num_simulations=mc_simulations,
        )

        # 11. Scenario Stress Tests
        stress_tests = ScenarioEngine.run_all_predefined_stress_tests(snapshot.positions)

        report = PortfolioComprehensiveReport(
            generated_at=datetime.now(timezone.utc),
            snapshot=snapshot,
            performance=performance,
            risk=risk,
            correlation=correlation,
            risk_attribution=attribution,
            optimization={
                "minimum_variance": min_vol,
                "maximum_sharpe": max_sharpe,
            },
            efficient_frontier=frontier,
            monte_carlo=monte_carlo,
            stress_tests=stress_tests,
        )

        logger.info(f"Analytics successfully calculated for portfolio ID={portfolio_id}")
        return report
