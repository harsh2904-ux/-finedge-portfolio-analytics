export interface AllocationMap {
  byAsset?: Record<string, number>;
  bySector?: Record<string, number>;
  byAssetClass?: Record<string, number>;
  [key: string]: any;
}

export interface CorrelationData {
  lookback: string;
  symbols: string[];
  matrix?: number[][];
  averageCorrelation?: number;
  correlation_matrix?: Record<string, Record<string, number>>;
  covariance_matrix?: Record<string, Record<string, number>>;
}

export interface ComponentRisk {
  symbol: string;
  weight: number;
  marginal_risk_contribution?: number;
  component_risk_contribution?: number;
  percent_risk_contribution?: number;
}

export interface RiskAttributionData {
  total_portfolio_risk?: number;
  contributions?: ComponentRisk[];
  percentages?: Record<string, number>;
  hhi?: number;
  effectiveConstituents?: number;
  topConcentration?: number;
}
