export interface AssetImpact {
  symbol: string;
  assetClass?: string;
  originalValue?: number;
  shockPercentage?: number;
  shockedValue?: number;
  pnl?: number;
}

export interface ScenarioResult {
  hypothetical?: boolean;
  currentValue?: number;
  scenarioValue?: number;
  absoluteChange?: number;
  percentageChange?: number;
  portfolioImpactPercent?: number;
  estimatedLoss?: number;
  assetImpacts?: AssetImpact[] | Record<string, number>;
}

export interface ScenarioRequest {
  scenarioName?: string;
  marketDropPercent?: number;
  interestRateBps?: number;
  volatilitySpikePercent?: number;
  shocks?: Record<string, number>;
}

export interface StressTestResult {
  scenarioName?: string;
  scenario_name?: string;
  category?: string;
  severity?: 'MILD' | 'MODERATE' | 'SEVERE' | 'CATASTROPHIC' | string;
  description?: string;
  portfolioImpactPercent?: number;
  portfolio_return?: number;
  portfolio_pnl?: number;
  estimatedLoss?: number;
  stressed_portfolio_value?: number;
  assetImpacts?: Record<string, number>;
  asset_shocks?: Record<string, number>;
}

export interface CachedResult<T> {
  calculatedAt: string;
  dataAsOf?: string;
  result: T;
}
