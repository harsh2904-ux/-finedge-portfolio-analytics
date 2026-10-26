export interface RiskMetrics {
  daily_volatility?: number;
  dailyVolatility?: number;
  annualized_volatility?: number;
  annualizedVolatility?: number;
  downside_deviation?: number;
  downsideDeviation?: number;
  sharpe_ratio?: number;
  sharpeRatio?: number;
  sortino_ratio?: number;
  sortinoRatio?: number;
  beta?: number;
  alpha?: number;
  skewness?: number;
  kurtosis?: number;
  historical_var_95?: number;
  historicalVar95?: number;
  historical_var_99?: number;
  historicalVar99?: number;
  parametric_var_95?: number;
  parametricVar95?: number;
  var95Daily?: number;
  parametric_var_99?: number;
  expected_shortfall_95?: number;
  expectedShortfall95?: number;
  cvar95Daily?: number;
  expected_shortfall_99?: number;
  max_drawdown?: number;
  maxDrawdown?: number;
  dollar_var_95?: number;
  dollar_expected_shortfall_95?: number;
}

export interface VarMetrics {
  confidenceLevel: number;
  portfolioValue: number;
  historicalVar: number;
  historicalVaR?: number;
  parametricVar: number;
  parametricVaR?: number;
  expectedShortfall: number;
  cvar?: number;
  dollarHistoricalVar: number;
  dollarParametricVar: number;
  dollarExpectedShortfall: number;
  monteCarloVaR?: number;
}

export interface DrawdownPoint {
  date: string;
  drawdown: number;
}

export interface DrawdownData {
  maxDrawdown: number;
  peakDate?: string;
  troughDate?: string;
  recoveryDate?: string;
  history?: DrawdownPoint[];
  dates?: string[];
  drawdowns?: number[];
}
