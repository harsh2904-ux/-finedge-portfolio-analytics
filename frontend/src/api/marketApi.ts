import { apiClient } from './axios';
import type { ApiResponse } from '../types/common';
import type { LatestPriceResponse, MarketHistoryResponse } from '../types/market';
import type { Asset, CreateAssetRequest } from '../types/asset';

export const marketApi = {
  getLatestPrice: async (symbol: string): Promise<LatestPriceResponse> => {
    const res = await apiClient.get<ApiResponse<LatestPriceResponse>>(`/market/${symbol}/latest`);
    return res.data.data;
  },

  getHistory: async (
    symbol: string,
    startDate?: string,
    endDate?: string,
    limit: number = 250
  ): Promise<MarketHistoryResponse> => {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    if (limit) params.append('limit', limit.toString());

    const res = await apiClient.get<ApiResponse<MarketHistoryResponse>>(
      `/market/${symbol}/history?${params.toString()}`
    );
    return res.data.data;
  },
};

export const assetApi = {
  getAssets: async (assetClass?: string, exchange?: string): Promise<Asset[]> => {
    const params = new URLSearchParams();
    if (assetClass && assetClass !== 'ALL') params.append('assetClass', assetClass);
    if (exchange) params.append('exchange', exchange);
    params.append('size', '100');

    const res = await apiClient.get<ApiResponse<any>>(`/assets?${params.toString()}`);
    const data = res.data?.data;
    if (Array.isArray(data)) {
      return data;
    }
    if (data && Array.isArray(data.content)) {
      return data.content;
    }
    return [];
  },

  getAsset: async (id: number): Promise<Asset> => {
    const res = await apiClient.get<ApiResponse<Asset>>(`/assets/${id}`);
    return res.data.data;
  },

  createAsset: async (data: CreateAssetRequest): Promise<Asset> => {
    const res = await apiClient.post<ApiResponse<Asset>>('/assets', data);
    return res.data.data;
  },
};
