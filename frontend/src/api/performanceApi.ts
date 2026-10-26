import { apiClient } from './axios';
import type { ApiResponse } from '../types/common';
import type { PerformanceMetrics } from '../types/performance';
import type { RiskMetrics, VarMetrics, DrawdownData } from '../types/risk';
import type { AllocationMap, CorrelationData, RiskAttributionData } from '../types/diversification';

export const performanceApi = {
  getPerformance: async (portfolioId: number, lookback: string = '1Y'): Promise<PerformanceMetrics> => {
    const res = await apiClient.get<ApiResponse<PerformanceMetrics>>(
      `/portfolios/${portfolioId}/performance?lookback=${lookback}`
    );
    return res.data.data;
  },
};

export const riskApi = {
  getRiskMetrics: async (portfolioId: number, lookback: string = '1Y'): Promise<RiskMetrics> => {
    const res = await apiClient.get<ApiResponse<RiskMetrics>>(
      `/portfolios/${portfolioId}/risk?lookback=${lookback}`
    );
    return res.data.data;
  },

  getVar: async (portfolioId: number, confidence: number = 0.95): Promise<VarMetrics> => {
    const res = await apiClient.get<ApiResponse<VarMetrics>>(
      `/portfolios/${portfolioId}/risk/var?confidence=${confidence}`
    );
    return res.data.data;
  },

  getDrawdown: async (portfolioId: number, lookback: string = '1Y'): Promise<DrawdownData> => {
    const res = await apiClient.get<ApiResponse<DrawdownData>>(
      `/portfolios/${portfolioId}/risk/drawdown?lookback=${lookback}`
    );
    return res.data.data;
  },

  // Diversification endpoints
  getAllocation: async (portfolioId: number): Promise<AllocationMap> => {
    const res = await apiClient.get<ApiResponse<AllocationMap>>(`/portfolios/${portfolioId}/allocation`);
    return res.data.data;
  },

  getCorrelation: async (portfolioId: number, lookback: string = '1Y'): Promise<CorrelationData> => {
    const res = await apiClient.get<ApiResponse<CorrelationData>>(
      `/portfolios/${portfolioId}/correlation?lookback=${lookback}`
    );
    return res.data.data;
  },

  getRiskContribution: async (portfolioId: number, lookback: string = '1Y'): Promise<RiskAttributionData> => {
    const res = await apiClient.get<ApiResponse<RiskAttributionData>>(
      `/portfolios/${portfolioId}/risk-contribution?lookback=${lookback}`
    );
    return res.data.data;
  },
};
