"""
CLI Script: Calculate Portfolio Analytics and Print Executive Report
Usage:
    python scripts/calculate_analytics.py --portfolio-id 1
"""

import argparse
import sys
from pathlib import Path

# Add src to sys.path
sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "src"))

from portfolio_analytics.database.connection import SessionFactory
from portfolio_analytics.pipelines.analytics_pipeline import AnalyticsPipeline
from portfolio_analytics.config.logging_config import logger


def parse_args():
    parser = argparse.ArgumentParser(description="Calculate portfolio risk, performance, and optimization analytics")
    parser.add_argument("--portfolio-id", type=int, default=1, help="Portfolio ID to analyze (default: 1)")
    parser.add_argument("--mc-sims", type=int, default=5000, help="Number of Monte Carlo simulations (default: 5000)")
    parser.add_argument("--output-json", type=str, default=None, help="Optional path to save report JSON")
    return parser.parse_args()


def main():
    args = parse_args()
    session = SessionFactory()
    pipeline = AnalyticsPipeline(session)

    try:
        report = pipeline.run_portfolio_analytics(
            portfolio_id=args.portfolio_id,
            mc_simulations=args.mc_sims,
        )

        snap = report.snapshot
        perf = report.performance
        risk = report.risk
        opt = report.optimization
        curr = snap.base_currency

        currency_symbol = "$" if curr == "USD" else ("₹" if curr == "INR" else f"{curr} ")

        print("\n" + "=" * 54)
        print("    MULTI-ASSET PORTFOLIO ANALYTICS REPORT")
        print("=" * 54)
        print(f"Portfolio:            {snap.name}")
        print(f"As of:                {snap.as_of_date.strftime('%Y-%m-%d')}")
        print(f"Base Currency:        {snap.base_currency}")
        print(f"Holdings Count:       {snap.holdings_count}")
        print("-" * 54)
        print(f"Portfolio Value:      {currency_symbol}{snap.total_market_value:,.2f}")
        print(f"Total Cost Basis:     {currency_symbol}{snap.total_cost_basis:,.2f}")
        print(f"Unrealized P&L:       {currency_symbol}{snap.total_unrealized_pnl:+,.2f} ({snap.total_unrealized_pnl_pct:+.2f}%)")
        print(f"Total Return:         {perf.total_return * 100:+.2f}%")
        print(f"CAGR:                 {perf.cagr * 100:.2f}%")
        print(f"Annualized Return:    {perf.annualized_return * 100:.2f}%")

        if perf.benchmark_return is not None:
            print(f"Benchmark Return:     {perf.benchmark_return * 100:+.2f}%")
            print(f"Excess Return:        {perf.excess_return * 100:+.2f}%")
            if perf.tracking_error is not None:
                print(f"Tracking Error:       {perf.tracking_error * 100:.2f}%")
            if perf.information_ratio is not None:
                print(f"Information Ratio:    {perf.information_ratio:.2f}")

        print("\n-------------------- RISK METRICS --------------------")
        print(f"Annualized Volatility:{risk.annualized_volatility * 100:.2f}%")
        print(f"Sharpe Ratio:         {risk.sharpe_ratio:.2f}")
        print(f"Sortino Ratio:        {risk.sortino_ratio:.2f}")
        print(f"Beta vs Benchmark:    {risk.beta if risk.beta is not None else 'N/A'}")
        if risk.alpha is not None:
            print(f"Jensen's Alpha:       {risk.alpha * 100:+.2f}%")
        print(f"Maximum Drawdown:     {risk.max_drawdown * 100:.2f}%")
        if risk.peak_date and risk.trough_date:
            print(f"  Peak to Trough:     {risk.peak_date} -> {risk.trough_date}")
        if risk.recovery_date:
            print(f"  Recovery Date:      {risk.recovery_date} ({risk.recovery_duration_days} days)")

        print(f"95% Historical VaR:   {currency_symbol}{risk.var_monetary_95:,.2f} ({risk.var_historical_95 * 100:.2f}%)")
        print(f"95% Expected Shortfall:{currency_symbol}{snap.total_market_value * risk.expected_shortfall_95:,.2f} ({risk.expected_shortfall_95 * 100:.2f}%)")

        print("\n----------------- PORTFOLIO OPTIMIZATION -------------")
        print(f"Current Return:       {perf.annualized_return * 100:.2f}%")
        print(f"Current Volatility:   {risk.annualized_volatility * 100:.2f}%")
        print(f"Current Sharpe:       {risk.sharpe_ratio:.2f}")
        print("")
        max_sh = opt.get("maximum_sharpe")
        if max_sh:
            print(f"Optimized (Max Sharpe) Return:     {max_sh.expected_annual_return * 100:.2f}%")
            print(f"Optimized (Max Sharpe) Volatility: {max_sh.expected_annual_volatility * 100:.2f}%")
            print(f"Optimized (Max Sharpe) Ratio:      {max_sh.sharpe_ratio:.2f}")
            print(f"Optimized Allocations:             {max_sh.weights}")

        print("\n-------------- MONTE CARLO (1-YEAR HORIZON) -----------")
        mc = report.monte_carlo
        print(f"Simulations Count:    {mc.num_simulations:,}")
        print(f"Expected Mean Ending: {currency_symbol}{mc.mean_ending_value:,.2f}")
        print(f"5th Percentile (Worst):{currency_symbol}{mc.p5_ending_value:,.2f}")
        print(f"95th Percentile (Best):{currency_symbol}{mc.p95_ending_value:,.2f}")
        print(f"Probability of Loss:  {mc.probability_of_loss * 100:.1f}%")

        print("\n---------------- STRESS TESTING SCENARIOS ------------")
        for sc in report.stress_tests:
            print(f"  {sc.scenario_name:<38}: {sc.percentage_change:+.2f}% ({currency_symbol}{sc.absolute_change:+,.2f})")

        print("=" * 54 + "\n")

        if args.output_json:
            with open(args.output_json, "w", encoding="utf-8") as f:
                f.write(report.model_dump_json(indent=2))
            print(f"Full analytics report JSON saved to: {args.output_json}\n")

    finally:
        session.close()


if __name__ == "__main__":
    main()
