export type OptimizationMethod = 'MAX_SHARPE' | 'MIN_VARIANCE' | 'EQUAL_WEIGHT';
export type OptimizationObjective = 'MAX_SHARPE' | 'MIN_VOLATILITY' | 'TARGET_RETURN';

export interface OptimizationRequest {
  objective?: OptimizationObjective;
  method?: OptimizationMethod;
  lookback?: string;
  minWeight?: number;
  maxWeight?: number;
  targetReturn?: number;
}

export interface EfficientFrontierRequest {
  points?: number;
  minWeight?: number;
  maxWeight?: number;
}

export interface AssetWeight {
  symbol: string;
  weight: number;
}

export interface PortfolioPointMetrics {
  volatility: number;
  expectedReturn: number;
  sharpeRatio: number;
  weights: Record<string, number>;
}

export interface OptimizationResult {
  method?: string;
  expectedReturn?: number;
  expectedVolatility?: number;
  sharpeRatio?: number;
  weights?: AssetWeight[] | Record<string, number>;
  calculatedAt?: string;
  current?: PortfolioPointMetrics;
  optimized?: PortfolioPointMetrics;
}

export interface FrontierPoint {
  volatility: number;
  expected_return?: number;
  expectedReturn?: number;
  sharpe_ratio?: number;
  sharpeRatio?: number;
  weights?: Record<string, number>;
}

export interface EfficientFrontierResult {
  simulations?: number;
  points?: FrontierPoint[];
  frontierPoints?: FrontierPoint[];
  minimum_volatility_point?: FrontierPoint;
  maximum_sharpe_point?: FrontierPoint;
  currentPoint?: { volatility: number; returnVal: number; sharpe: number };
  maxSharpePoint?: { volatility: number; returnVal: number; sharpe: number };
  minVolPoint?: { volatility: number; returnVal: number; sharpe: number };
  assetPoints?: { symbol: string; volatility: number; returnVal: number }[];
}
