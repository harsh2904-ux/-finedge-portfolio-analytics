"""
FastAPI Microservice for Quantitative Financial Analytics.
Exposes Part 1 quantitative models to Spring Boot enterprise backend.
"""

from datetime import datetime, timezone, timedelta
from typing import Dict, List, Optional, Any
from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

from portfolio_analytics.database.connection import SessionFactory
from portfolio_analytics.portfolio.portfolio_service import PortfolioService
from portfolio_analytics.analytics.performance import PerformanceCalculator
from portfolio_analytics.analytics.risk import RiskCalculator
from portfolio_analytics.analytics.beta import BetaCalculator
from portfolio_analytics.analytics.correlation import CorrelationCalculator
from portfolio_analytics.analytics.attribution import RiskAttributionCalculator
from portfolio_analytics.analytics.drawdown import DrawdownCalculator
from portfolio_analytics.optimization.minimum_variance import MinimumVarianceOptimizer
from portfolio_analytics.optimization.maximum_sharpe import MaximumSharpeOptimizer
from portfolio_analytics.optimization.efficient_frontier import EfficientFrontierEngine
from portfolio_analytics.optimization.monte_carlo import MonteCarloEngine
from portfolio_analytics.scenarios.scenario_engine import ScenarioEngine, ScenarioResult
from portfolio_analytics.config.settings import settings
from portfolio_analytics.config.logging_config import logger

app = FastAPI(
    title="Portfolio Analytics Engine API",
    version="2.0.0",
    description="Quantitative Financial Analytics Microservice for Multi-Asset Portfolio Analytics",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


def _parse_lookback(lookback: Optional[str]) -> Optional[datetime]:
    if not lookback:
        return None
    now = datetime.now(timezone.utc)
    lookback = lookback.upper()
    if lookback == "1M":
        return now - timedelta(days=30)
    elif lookback == "3M":
        return now - timedelta(days=90)
    elif lookback == "6M":
        return now - timedelta(days=180)
    elif lookback == "1Y":
        return now - timedelta(days=365)
    elif lookback == "3Y":
        return now - timedelta(days=365 * 3)
    elif lookback == "5Y":
        return now - timedelta(days=365 * 5)
    elif lookback == "MAX":
        return None
    return None


# --- Request DTOs ---

class AnalyticsBaseRequest(BaseModel):
    portfolioId: int
    lookback: Optional[str] = "1Y"
    startDate: Optional[datetime] = None
    endDate: Optional[datetime] = None
    riskFreeRate: Optional[float] = None
    tradingDays: Optional[int] = 252


class VaRRequest(AnalyticsBaseRequest):
    confidenceLevel: float = Field(default=0.95, ge=0.80, le=0.999)


class OptimizationRequest(BaseModel):
    portfolioId: int
    method: str = Field(default="MAX_SHARPE", description="MAX_SHARPE, MIN_VARIANCE, or EQUAL_WEIGHT")
    lookback: Optional[str] = "3Y"
    maxWeight: float = Field(default=1.0, ge=0.01, le=1.0)
    riskFreeRate: Optional[float] = None


class EfficientFrontierRequest(BaseModel):
    portfolioId: int
    simulations: int = Field(default=2000, ge=100, le=20000)
    lookback: Optional[str] = "3Y"
    riskFreeRate: Optional[float] = None


class MonteCarloRequest(BaseModel):
    portfolioId: int
    simulations: int = Field(default=5000, ge=100, le=50000)
    horizonDays: int = Field(default=252, ge=5, le=2520)


class ScenarioShockRequest(BaseModel):
    portfolioId: int
    shocks: Dict[str, float] = Field(..., description="Map of AssetClass or Symbol to decimal shock (e.g. {'EQUITY': -0.30})")


class StressTestRunRequest(BaseModel):
    portfolioId: int
    scenarios: Optional[List[str]] = None


# --- Endpoints ---

@app.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "Python Analytics Engine",
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }


@app.post("/analytics/performance")
def get_performance(req: AnalyticsBaseRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        start_date = req.startDate or _parse_lookback(req.lookback)
        port_series, bench_series, _ = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=start_date,
            end_date=req.endDate,
        )
        if port_series.empty or len(port_series) < 2:
            raise HTTPException(status_code=400, detail="Insufficient price history for portfolio.")

        trading_days = req.tradingDays or settings.TRADING_DAYS_PER_YEAR
        metrics = PerformanceCalculator.analyze_performance(
            portfolio_series=port_series,
            benchmark_series=bench_series,
            trading_days=trading_days,
        )
        return metrics.model_dump()
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error calculating performance: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/risk")
def get_risk(req: AnalyticsBaseRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)
        start_date = req.startDate or _parse_lookback(req.lookback)
        port_series, bench_series, _ = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=start_date,
            end_date=req.endDate,
        )
        if port_series.empty or len(port_series) < 5:
            raise HTTPException(status_code=400, detail="Insufficient price history for risk calculation.")

        rf = req.riskFreeRate if req.riskFreeRate is not None else settings.DEFAULT_RISK_FREE_RATE
        td = req.tradingDays or settings.TRADING_DAYS_PER_YEAR

        perf = PerformanceCalculator.analyze_performance(port_series, bench_series, trading_days=td)
        port_daily_ret = port_series.pct_change().dropna()
        bench_daily_ret = bench_series.pct_change().dropna() if not bench_series.empty else None

        beta, alpha = BetaCalculator.calculate_beta_and_alpha(
            portfolio_returns=port_daily_ret,
            benchmark_returns=bench_daily_ret,
            risk_free_rate=rf,
            trading_days=td,
        )

        risk = RiskCalculator.analyze_risk(
            portfolio_prices=port_series,
            current_portfolio_value=snapshot.total_market_value,
            annualized_return=perf.annualized_return,
            beta=beta,
            alpha=alpha,
            risk_free_rate=rf,
            trading_days=td,
        )
        return risk.model_dump()
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error calculating risk: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/drawdown")
def get_drawdown(req: AnalyticsBaseRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        start_date = req.startDate or _parse_lookback(req.lookback)
        port_series, _, _ = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=start_date,
            end_date=req.endDate,
        )
        if port_series.empty:
            raise HTTPException(status_code=400, detail="Insufficient price history.")

        mdd, peak_dt, trough_dt, rec_dt = DrawdownCalculator.calculate_drawdown_metrics(port_series)
        dd_series = DrawdownCalculator.calculate_drawdown_series(port_series)

        return {
            "maxDrawdown": mdd,
            "peakDate": peak_dt.isoformat() if peak_dt else None,
            "troughDate": trough_dt.isoformat() if trough_dt else None,
            "recoveryDate": rec_dt.isoformat() if rec_dt else None,
            "history": [
                {"date": d.isoformat(), "drawdown": float(v)}
                for d, v in dd_series.items()
            ][-100:],  # return last 100 observations
        }
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error calculating drawdown: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/var")
def get_var(req: VaRRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)
        start_date = req.startDate or _parse_lookback(req.lookback)
        port_series, _, _ = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=start_date,
            end_date=req.endDate,
        )
        if port_series.empty:
            raise HTTPException(status_code=400, detail="Insufficient price history.")

        daily_returns = port_series.pct_change().dropna()
        h_var = RiskCalculator.calculate_historical_var(daily_returns, req.confidenceLevel)
        p_var = RiskCalculator.calculate_parametric_var(daily_returns, req.confidenceLevel)
        c_var = RiskCalculator.calculate_expected_shortfall(daily_returns, req.confidenceLevel)

        val = snapshot.total_market_value
        return {
            "confidenceLevel": req.confidenceLevel,
            "portfolioValue": val,
            "historicalVar": h_var,
            "parametricVar": p_var,
            "expectedShortfall": c_var,
            "dollarHistoricalVar": round(h_var * val, 2),
            "dollarParametricVar": round(p_var * val, 2),
            "dollarExpectedShortfall": round(c_var * val, 2),
        }
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error calculating VaR: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/correlation")
def get_correlation(req: AnalyticsBaseRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)
        start_date = req.startDate or _parse_lookback(req.lookback)
        _, _, price_matrix = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=start_date,
            end_date=req.endDate,
        )
        held_symbols = [p.symbol for p in snapshot.positions if p.symbol in price_matrix.columns]
        if len(held_symbols) < 2:
            raise HTTPException(status_code=400, detail="At least 2 distinct assets are required for correlation.")

        held_matrix = price_matrix[held_symbols]
        corr_res = CorrelationCalculator.analyze_correlation(held_matrix, lookback=req.lookback or "1Y")
        return corr_res.model_dump()
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error calculating correlation: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/risk-contribution")
def get_risk_contribution(req: AnalyticsBaseRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)
        start_date = req.startDate or _parse_lookback(req.lookback)
        _, _, price_matrix = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=start_date,
            end_date=req.endDate,
        )
        held_symbols = [p.symbol for p in snapshot.positions if p.symbol in price_matrix.columns]
        if len(held_symbols) < 2:
            raise HTTPException(status_code=400, detail="At least 2 distinct assets required.")

        held_matrix = price_matrix[held_symbols]
        weights = {p.symbol: p.actual_weight for p in snapshot.positions if p.symbol in held_symbols}
        res = RiskAttributionCalculator.calculate_risk_attribution(held_matrix, weights)
        return res.model_dump()
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error calculating risk contribution: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/optimization")
def run_optimization(req: OptimizationRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)
        start_date = _parse_lookback(req.lookback)
        _, _, price_matrix = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=start_date,
        )
        held_symbols = [p.symbol for p in snapshot.positions if p.symbol in price_matrix.columns]
        if len(held_symbols) < 2:
            raise HTTPException(status_code=400, detail="At least 2 distinct assets required for optimization.")

        held_matrix = price_matrix[held_symbols]
        rf = req.riskFreeRate if req.riskFreeRate is not None else settings.DEFAULT_RISK_FREE_RATE

        method = req.method.upper()
        if method == "MIN_VARIANCE":
            opt = MinimumVarianceOptimizer.optimize(held_matrix, max_weight=req.maxWeight, risk_free_rate=rf)
        elif method == "EQUAL_WEIGHT":
            num = len(held_symbols)
            eq_w = {s: round(1.0 / num, 4) for s in held_symbols}
            returns_df = held_matrix.pct_change().dropna()
            mean_ret = returns_df.mean() * 252.0
            cov_mat = returns_df.cov() * 252.0
            w_arr = [1.0 / num] * num
            exp_ret = float(sum(w_arr[i] * mean_ret.iloc[i] for i in range(num)))
            import numpy as np
            exp_vol = float(np.sqrt(np.dot(w_arr, np.dot(cov_mat.values, w_arr))))
            sharpe = (exp_ret - rf) / exp_vol if exp_vol > 0 else 0.0
            from portfolio_analytics.optimization.models import OptimizationResult
            opt = OptimizationResult(
                method="Equal Weight",
                weights=eq_w,
                expected_annual_return=round(exp_ret, 6),
                expected_annual_volatility=round(exp_vol, 6),
                sharpe_ratio=round(sharpe, 4),
            )
        else:
            opt = MaximumSharpeOptimizer.optimize(held_matrix, risk_free_rate=rf, max_weight=req.maxWeight)

        result_dict = opt.model_dump()
        result_dict["calculatedAt"] = datetime.now(timezone.utc).isoformat()
        return result_dict
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error running optimization: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/efficient-frontier")
def run_efficient_frontier(req: EfficientFrontierRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)
        start_date = _parse_lookback(req.lookback)
        _, _, price_matrix = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=start_date,
        )
        held_symbols = [p.symbol for p in snapshot.positions if p.symbol in price_matrix.columns]
        if len(held_symbols) < 2:
            raise HTTPException(status_code=400, detail="At least 2 distinct assets required.")

        held_matrix = price_matrix[held_symbols]
        rf = req.riskFreeRate if req.riskFreeRate is not None else settings.DEFAULT_RISK_FREE_RATE
        ef = EfficientFrontierEngine.generate_frontier(held_matrix, num_portfolios=req.simulations, risk_free_rate=rf)
        return ef.model_dump()
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error calculating efficient frontier: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/monte-carlo")
def run_monte_carlo(req: MonteCarloRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)
        _, _, price_matrix = portfolio_service.get_historical_portfolio_and_benchmark(
            portfolio_id=req.portfolioId,
            start_date=_parse_lookback("1Y"),
        )
        held_symbols = [p.symbol for p in snapshot.positions if p.symbol in price_matrix.columns]
        held_matrix = price_matrix[held_symbols]
        weights = {p.symbol: p.actual_weight for p in snapshot.positions if p.symbol in held_symbols}

        mc = MonteCarloEngine.simulate(
            price_matrix=held_matrix,
            weights=weights,
            initial_value=snapshot.total_market_value,
            time_horizon_days=req.horizonDays,
            num_simulations=req.simulations,
        )
        return mc.model_dump()
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error running Monte Carlo simulation: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/scenario")
def run_custom_scenario(req: ScenarioShockRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)

        # Apply shocks either by asset_class or symbol
        current_val = snapshot.total_market_value
        shocked_val = 0.0
        impacts = []

        for pos in snapshot.positions:
            shock = req.shocks.get(pos.asset_class.upper(), req.shocks.get(pos.symbol.upper(), 0.0))
            pos_shocked = pos.current_market_value * (1.0 + shock)
            shocked_val += pos_shocked
            impacts.append({
                "symbol": pos.symbol,
                "assetClass": pos.asset_class,
                "originalValue": pos.current_market_value,
                "shockPercentage": shock,
                "shockedValue": pos_shocked,
                "pnl": pos_shocked - pos.current_market_value,
            })

        abs_change = shocked_val - current_val
        pct_change = (abs_change / current_val) if current_val > 0 else 0.0

        return {
            "hypothetical": True,
            "currentValue": round(current_val, 2),
            "scenarioValue": round(shocked_val, 2),
            "absoluteChange": round(abs_change, 2),
            "percentageChange": round(pct_change, 4),
            "assetImpacts": impacts,
        }
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error running scenario: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()


@app.post("/analytics/stress-test")
def run_stress_test(req: StressTestRunRequest):
    session = SessionFactory()
    try:
        portfolio_service = PortfolioService(session)
        snapshot = portfolio_service.get_current_snapshot(req.portfolioId)

        results = ScenarioEngine.run_all_predefined_stress_tests(snapshot.positions)
        if req.scenarios:
            allowed = set(s.upper() for s in req.scenarios)
            results = [r for r in results if r.scenario_name.upper().replace(" ", "_") in allowed or r.scenario_name in allowed]

        return [r.model_dump() for r in results]
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error running stress tests: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        session.close()
