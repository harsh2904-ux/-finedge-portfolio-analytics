"""
Unit Tests for Scenario Analysis & Stress Testing Engine
"""

import numpy as np
from portfolio_analytics.scenarios.scenario_engine import ScenarioEngine


def test_custom_scenario_shock_math(sample_positions):
    # Total original value = 18000 (AAPL) + 12000 (BND) = 30000
    shocks = {"AAPL": -0.10, "BND": +0.05}
    res = ScenarioEngine.run_custom_scenario(
        positions=sample_positions,
        scenario_name="Custom Shock Test",
        description="Test description",
        shocks=shocks,
    )

    assert res.original_portfolio_value == 30000.0
    # Expected: AAPL -> 18000 * 0.9 = 16200, BND -> 12000 * 1.05 = 12600. Total = 28800.
    assert np.isclose(res.scenario_portfolio_value, 28800.0)
    assert np.isclose(res.absolute_change, -1200.0)
    assert np.isclose(res.percentage_change, -4.0)


def test_predefined_stress_scenarios_execution(sample_positions):
    stress_results = ScenarioEngine.run_all_predefined_stress_tests(sample_positions)
    assert len(stress_results) == 4
    for r in stress_results:
        assert r.is_hypothetical is True
        assert len(r.position_impacts) == len(sample_positions)
