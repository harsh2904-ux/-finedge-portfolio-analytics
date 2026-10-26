export interface MonteCarloRequest {
  numSimulations?: number;
  simulations?: number;
  timeHorizonDays?: number;
  horizonDays?: number;
  distribution?: 'GBM' | 'STUDENT_T';
}

export interface MonteCarloPercentiles {
  days?: number[];
  p5?: number[];
  p25?: number[];
  p50?: number[];
  p75?: number[];
  p95?: number[];
  [key: string]: any;
}

export interface MonteCarloResult {
  simulations: number;
  time_horizon_days: number;
  initial_portfolio_value?: number;
  mean_ending_value: number;
  median_ending_value: number;
  medianTerminalValue?: number;
  worst5PercentileValue?: number;
  probability_of_loss: number;
  probLoss?: number;
  terminalVar95?: number;
  percentiles?: MonteCarloPercentiles;
}
