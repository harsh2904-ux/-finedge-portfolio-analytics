"""
Scenario Analysis & Stress Testing Engine: Custom Shocks and Macroeconomic Stress Tests
"""

from typing import Dict, List, Optional
from pydantic import BaseModel, Field
from portfolio_analytics.portfolio.holdings import HoldingValuation


class PositionScenarioImpact(BaseModel):
    """Impact of a scenario shock on an individual holding."""
    symbol: str
    asset_class: str
    original_value: float
    shock_applied_pct: float
    scenario_value: float
    absolute_change: float
    percentage_change: float


class ScenarioResult(BaseModel):
    """Result of a hypothetical stress test or custom scenario shock."""
    scenario_name: str
    description: str
    original_portfolio_value: float
    scenario_portfolio_value: float
    absolute_change: float
    percentage_change: float
    is_hypothetical: bool = True
    position_impacts: List[PositionScenarioImpact]


class ScenarioEngine:
    """
    Simulates macroeconomic stress tests and custom asset-class or ticker shocks.
    """

    PREDEFINED_STRESS_SCENARIOS = {
        "Global Financial Crash (2008/2020 Style)": {
            "description": "Severe liquidity crisis and market panic across growth equities and real estate.",
            "shocks": {
                "Equity": -0.30,
                "Equity / ETF": -0.28,
                "Real Estate / REIT": -0.25,
                "Commodity / Gold ETF": 0.10,
                "Fixed Income / Bond ETF": 0.05,
            },
        },
        "Bull Market Liquidity Rally": {
            "description": "Central bank easing and rapid economic expansion.",
            "shocks": {
                "Equity": 0.25,
                "Equity / ETF": 0.22,
                "Real Estate / REIT": 0.20,
                "Commodity / Gold ETF": -0.05,
                "Fixed Income / Bond ETF": -0.03,
            },
        },
        "Stagflation & Supply Shock": {
            "description": "Persistently high inflation with slowing GDP growth.",
            "shocks": {
                "Equity": -0.15,
                "Equity / ETF": -0.12,
                "Real Estate / REIT": -0.08,
                "Commodity / Gold ETF": 0.25,
                "Fixed Income / Bond ETF": -0.10,
            },
        },
        "Aggressive Central Bank Rate Hike Shock": {
            "description": "Rapid monetary tightening impacting duration assets and real estate.",
            "shocks": {
                "Equity": -0.12,
                "Equity / ETF": -0.10,
                "Real Estate / REIT": -0.18,
                "Commodity / Gold ETF": 0.05,
                "Fixed Income / Bond ETF": -0.14,
            },
        },
    }

    @classmethod
    def run_custom_scenario(
        cls,
        positions: List[HoldingValuation],
        scenario_name: str,
        description: str,
        shocks: Dict[str, float],
    ) -> ScenarioResult:
        """
        Applies custom shocks to a portfolio.
        Shocks can be mapped by ticker symbol (e.g. 'AAPL': -0.20)
        or by asset_class (e.g. 'Equity': -0.30).
        """
        original_total = 0.0
        scenario_total = 0.0
        impacts: List[PositionScenarioImpact] = []

        for p in positions:
            orig_val = p.current_market_value
            original_total += orig_val

            # Determine shock percentage: Symbol takes priority, then Asset Class, default 0.0
            shock_pct = shocks.get(p.symbol, shocks.get(p.asset_class, 0.0))

            scen_val = orig_val * (1.0 + shock_pct)
            scenario_total += scen_val
            abs_change = scen_val - orig_val

            impacts.append(
                PositionScenarioImpact(
                    symbol=p.symbol,
                    asset_class=p.asset_class,
                    original_value=round(orig_val, 2),
                    shock_applied_pct=round(shock_pct * 100.0, 2),
                    scenario_value=round(scen_val, 2),
                    absolute_change=round(abs_change, 2),
                    percentage_change=round(shock_pct * 100.0, 2),
                )
            )

        total_abs_change = scenario_total - original_total
        total_pct_change = (total_abs_change / original_total * 100.0) if original_total > 0 else 0.0

        return ScenarioResult(
            scenario_name=scenario_name,
            description=description,
            original_portfolio_value=round(original_total, 2),
            scenario_portfolio_value=round(scenario_total, 2),
            absolute_change=round(total_abs_change, 2),
            percentage_change=round(total_pct_change, 2),
            is_hypothetical=True,
            position_impacts=impacts,
        )

    @classmethod
    def run_all_predefined_stress_tests(
        cls,
        positions: List[HoldingValuation],
    ) -> List[ScenarioResult]:
        """
        Runs all predefined institutional stress tests against current holdings.
        """
        results: List[ScenarioResult] = []
        for name, data in cls.PREDEFINED_STRESS_SCENARIOS.items():
            res = cls.run_custom_scenario(
                positions=positions,
                scenario_name=name,
                description=data["description"],
                shocks=data["shocks"],
            )
            results.append(res)
        return results
