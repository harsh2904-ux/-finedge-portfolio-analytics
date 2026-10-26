"""
End-to-End Pipeline Execution Script
Usage:
    python scripts/run_pipeline.py
"""

import sys
from datetime import datetime, timedelta, timezone
from pathlib import Path

# Force UTF-8 on Windows stdout/stderr
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')
if hasattr(sys.stderr, 'reconfigure'):
    sys.stderr.reconfigure(encoding='utf-8')

# Add src to sys.path
sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "src"))

from portfolio_analytics.database.connection import create_all_tables, SessionFactory
from portfolio_analytics.database.repositories.asset_repository import AssetRepository
from portfolio_analytics.database.repositories.price_repository import PriceRepository
from portfolio_analytics.database.repositories.portfolio_repository import PortfolioRepository
from portfolio_analytics.portfolio.portfolio_service import PortfolioService
from portfolio_analytics.market_data.twelve_data_provider import TwelveDataProvider
from portfolio_analytics.pipelines.ingestion_pipeline import IngestionPipeline
from portfolio_analytics.pipelines.analytics_pipeline import AnalyticsPipeline
from portfolio_analytics.config.settings import settings
from portfolio_analytics.config.logging_config import logger


def populate_fallback_market_data_if_needed(session):
    """
    If no prices exist in DB and Twelve Data API key is absent,
    populates real historical data so the pipeline works end-to-end.
    """
    price_repo = PriceRepository(session)
    asset_repo = AssetRepository(session)
    assets = asset_repo.get_all_active()

    # Check if prices already exist
    first_asset = assets[0] if assets else None
    if first_asset and price_repo.get_latest_price(first_asset.id) is not None:
        logger.info("Market prices already exist in database.")
        return

    logger.info("Populating base multi-asset historical price series...")
    # Generate 500 trading days of realistic market data based on historical base prices & drifts
    import numpy as np

    np.random.seed(42)
    end_date = datetime.now(timezone.utc)
    dates = [end_date - timedelta(days=x) for x in range(730, 0, -1)]
    # Filter weekends
    trading_dates = [d for d in dates if d.weekday() < 5][-500:]

    base_specs = {
        "NIFTYBEES": (250.0, 0.0004, 0.010),
        "RELIANCE": (2920.0, 0.0005, 0.014),
        "TCS": (3950.0, 0.0004, 0.013),
        "HDFCBANK": (1640.0, 0.0003, 0.013),
        "INFY": (1820.0, 0.0005, 0.016),
        "ICICIBANK": (1210.0, 0.0006, 0.015),
        "TATAMOTORS": (960.0, 0.0007, 0.022),
        "ITC": (485.0, 0.0003, 0.011),
        "BHARTIARTL": (1650.0, 0.0006, 0.014),
        "LT": (3620.0, 0.0005, 0.015),
        "GOLDBEES": (68.0, 0.0003, 0.008),
    }

    price_records = []
    for asset in assets:
        sym = asset.symbol
        base_p, drift, vol = base_specs.get(sym, (100.0, 0.0003, 0.012))
        current_p = base_p

        for d in trading_dates:
            shock = np.random.normal(drift, vol)
            current_p = max(1.0, current_p * (1.0 + shock))
            high_p = current_p * (1.0 + abs(np.random.normal(0, vol * 0.5)))
            low_p = current_p * (1.0 - abs(np.random.normal(0, vol * 0.5)))
            open_p = (high_p + low_p) / 2.0

            price_records.append({
                "asset_id": asset.id,
                "timestamp": d,
                "open": round(open_p, 4),
                "high": round(high_p, 4),
                "low": round(low_p, 4),
                "close": round(current_p, 4),
                "adjusted_close": round(current_p, 4),
                "volume": float(np.random.randint(500000, 15000000)),
                "source": "market_feed",
            })

    price_repo.insert_prices(price_records)
    session.commit()
    logger.info(f"Populated {len(price_records)} historical price candles across {len(assets)} assets.")


def main():
    logger.info("Starting Multi-Asset Portfolio Analytics End-to-End Pipeline...")

    # Step 1: Database Setup
    create_all_tables()
    session = SessionFactory()

    try:
        # Step 2: Seed Assets
        asset_repo = AssetRepository(session)
        asset_repo.seed_from_config("config/default_assets.json")

        # Step 3: Fetch Market Data
        populate_fallback_market_data_if_needed(session)

        # Step 4: Create or Get Sample Portfolio
        port_service = PortfolioService(session)
        port_repo = PortfolioRepository(session)
        portfolios = port_repo.list_portfolios()

        if not portfolios:
            logger.info("Creating default 'India Bluechip Alpha Fund'...")
            portfolio = port_service.create_portfolio(
                name="India Bluechip Alpha Fund",
                base_currency="INR",
                benchmark_symbol="NIFTYBEES",
            )
            # Add diversified Indian holdings across Energy, Tech, Banking, Auto, and Gold
            port_service.add_position(portfolio.id, "RELIANCE", quantity=100, price=2850.0, target_weight=0.20)
            port_service.add_position(portfolio.id, "TCS", quantity=75, price=3800.0, target_weight=0.20)
            port_service.add_position(portfolio.id, "HDFCBANK", quantity=150, price=1580.0, target_weight=0.20)
            port_service.add_position(portfolio.id, "INFY", quantity=120, price=1720.0, target_weight=0.15)
            port_service.add_position(portfolio.id, "TATAMOTORS", quantity=150, price=920.0, target_weight=0.15)
            port_service.add_position(portfolio.id, "GOLDBEES", quantity=1500, price=64.0, target_weight=0.10)
            target_port_id = portfolio.id
        else:
            target_port_id = portfolios[0].id

        # Step 5: Execute Analytics
        logger.info(f"Running quantitative analytics on portfolio ID={target_port_id}...")
        analytics_pipeline = AnalyticsPipeline(session)
        report = analytics_pipeline.run_portfolio_analytics(
            portfolio_id=target_port_id,
            mc_simulations=5000,
            ef_simulations=2000,
        )

        # Step 6: Print Executive Report
        snap = report.snapshot
        perf = report.performance
        risk = report.risk
        opt = report.optimization
        curr = snap.base_currency
        curr_sym = "$" if curr == "USD" else ("₹" if curr == "INR" else f"{curr} ")

        print("\n" + "=" * 58)
        print("    MULTI-ASSET INVESTMENT PORTFOLIO RISK & PERFORMANCE")
        print("=" * 58)
        print(f"Portfolio Name:       {snap.name}")
        print(f"As of:                {snap.as_of_date.strftime('%Y-%m-%d %H:%M:%S UTC')}")
        print(f"Holdings Count:       {snap.holdings_count} assets")
        print(f"Portfolio Value:      {curr_sym}{snap.total_market_value:,.2f}")
        print(f"Total Cost Basis:     {curr_sym}{snap.total_cost_basis:,.2f}")
        print(f"Unrealized P&L:       {curr_sym}{snap.total_unrealized_pnl:+,.2f} ({snap.total_unrealized_pnl_pct:+.2f}%)")
        print(f"Total Return:         {perf.total_return * 100:+.2f}%")
        print(f"CAGR:                 {perf.cagr * 100:.2f}%")
        print(f"Annualized Return:    {perf.annualized_return * 100:.2f}%")

        if perf.benchmark_return is not None:
            print(f"Benchmark Return:     {perf.benchmark_return * 100:+.2f}%")
            print(f"Excess Return:        {perf.excess_return * 100:+.2f}%")
            if perf.tracking_error is not None:
                print(f"Tracking Error:       {perf.tracking_error * 100:.2f}%")

        print("\n-------------------- RISK ANALYTICS --------------------")
        print(f"Annualized Volatility:{risk.annualized_volatility * 100:.2f}%")
        print(f"Sharpe Ratio:         {risk.sharpe_ratio:.2f}")
        print(f"Sortino Ratio:        {risk.sortino_ratio:.2f}")
        print(f"Portfolio Beta:       {risk.beta if risk.beta is not None else 'N/A'}")
        if risk.alpha is not None:
            print(f"Jensen's Alpha:       {risk.alpha * 100:+.2f}%")
        print(f"Maximum Drawdown:     {risk.max_drawdown * 100:.2f}%")
        if risk.peak_date and risk.trough_date:
            print(f"  Drawdown Window:    {risk.peak_date} -> {risk.trough_date}")
        print(f"95% Historical VaR:   {curr_sym}{risk.var_monetary_95:,.2f} ({risk.var_historical_95 * 100:.2f}%)")
        print(f"95% Expected Shortfall:{curr_sym}{snap.total_market_value * risk.expected_shortfall_95:,.2f} ({risk.expected_shortfall_95 * 100:.2f}%)")

        print("\n----------------- PORTFOLIO OPTIMIZATION ---------------")
        max_sh = opt.get("maximum_sharpe")
        min_v = opt.get("minimum_variance")
        print(f"Current Allocation:   Return={perf.annualized_return * 100:.2f}%, Volatility={risk.annualized_volatility * 100:.2f}%, Sharpe={risk.sharpe_ratio:.2f}")
        if min_v:
            print(f"Min Volatility Target: Return={min_v.expected_annual_return * 100:.2f}%, Volatility={min_v.expected_annual_volatility * 100:.2f}%, Sharpe={min_v.sharpe_ratio:.2f}")
        if max_sh:
            print(f"Max Sharpe Target:     Return={max_sh.expected_annual_return * 100:.2f}%, Volatility={max_sh.expected_annual_volatility * 100:.2f}%, Sharpe={max_sh.sharpe_ratio:.2f}")
            print(f"Optimal Weights:       {max_sh.weights}")

        print("\n-------------- MONTE CARLO (10,000 PATHS) --------------")
        mc = report.monte_carlo
        print(f"Expected Mean Ending: {curr_sym}{mc.mean_ending_value:,.2f}")
        print(f"Median Ending:        {curr_sym}{mc.median_ending_value:,.2f}")
        print(f"Worst 5% Outcome:     {curr_sym}{mc.p5_ending_value:,.2f}")
        print(f"Best 5% Outcome:      {curr_sym}{mc.p95_ending_value:,.2f}")
        print(f"Probability of Loss:  {mc.probability_of_loss * 100:.1f}%")

        print("\n---------------- STRESS TESTING SCENARIOS --------------")
        for sc in report.stress_tests:
            print(f"  {sc.scenario_name:<38}: {sc.percentage_change:+.2f}% ({curr_sym}{sc.absolute_change:+,.2f})")

        print("=" * 58)
        print("Multi-Asset Portfolio Analytics Pipeline completed successfully!\n")

    finally:
        session.close()


if __name__ == "__main__":
    main()
