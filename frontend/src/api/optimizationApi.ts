import { apiClient } from './axios';
import type { ApiResponse } from '../types/common';
import type {
  OptimizationRequest,
  OptimizationResult,
  EfficientFrontierRequest,
  EfficientFrontierResult,
} from '../types/optimization';
import type { MonteCarloRequest, MonteCarloResult } from '../types/simulation';
import type {
  ScenarioRequest,
  ScenarioResult,
  StressTestResult,
  CachedResult,
} from '../types/scenario';

export const optimizationApi = {
  optimize: async (
    portfolioId: number,
    data: OptimizationRequest
  ): Promise<CachedResult<OptimizationResult>> => {
    const res = await apiClient.post<ApiResponse<CachedResult<OptimizationResult>>>(
      `/portfolios/${portfolioId}/optimization`,
      data
    );
    return res.data.data;
  },

  getEfficientFrontier: async (
    portfolioId: number,
    data: EfficientFrontierRequest
  ): Promise<CachedResult<EfficientFrontierResult>> => {
    const res = await apiClient.post<ApiResponse<CachedResult<EfficientFrontierResult>>>(
      `/portfolios/${portfolioId}/efficient-frontier`,
      data
    );
    return res.data.data;
  },

  runMonteCarlo: async (
    portfolioId: number,
    data: MonteCarloRequest
  ): Promise<CachedResult<MonteCarloResult>> => {
    const res = await apiClient.post<ApiResponse<CachedResult<MonteCarloResult>>>(
      `/portfolios/${portfolioId}/monte-carlo`,
      data
    );
    return res.data.data;
  },
};

export const scenarioApi = {
  runScenario: async (
    portfolioId: number,
    data: ScenarioRequest
  ): Promise<ScenarioResult> => {
    const res = await apiClient.post<ApiResponse<ScenarioResult>>(
      `/portfolios/${portfolioId}/scenario`,
      data
    );
    return res.data.data;
  },

  getPredefinedStressTests: async (
    portfolioId: number
  ): Promise<CachedResult<StressTestResult[]>> => {
    const res = await apiClient.get<ApiResponse<CachedResult<StressTestResult[]>>>(
      `/portfolios/${portfolioId}/stress-tests`
    );
    return res.data.data;
  },

  runSelectedStressTests: async (
    portfolioId: number,
    scenarios?: string[]
  ): Promise<StressTestResult[]> => {
    const res = await apiClient.post<ApiResponse<StressTestResult[]>>(
      `/portfolios/${portfolioId}/stress-tests/run`,
      { scenarios }
    );
    return res.data.data;
  },
};
