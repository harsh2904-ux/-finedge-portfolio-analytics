import { apiClient } from './axios';
import type { ApiResponse, PagedResponse } from '../types/common';
import type {
  Portfolio,
  CreatePortfolioRequest,
  PortfolioValuation,
  Holding,
  CreateHoldingRequest,
  UpdateHoldingRequest,
  Transaction,
  CreateTransactionRequest,
} from '../types/portfolio';

export const portfolioApi = {
  // Portfolios
  getPortfolios: async (): Promise<Portfolio[]> => {
    const res = await apiClient.get<ApiResponse<Portfolio[]>>('/portfolios');
    return res.data.data;
  },

  getPortfolio: async (id: number): Promise<Portfolio> => {
    const res = await apiClient.get<ApiResponse<Portfolio>>(`/portfolios/${id}`);
    return res.data.data;
  },

  createPortfolio: async (data: CreatePortfolioRequest): Promise<Portfolio> => {
    const res = await apiClient.post<ApiResponse<Portfolio>>('/portfolios', data);
    return res.data.data;
  },

  updatePortfolio: async (id: number, data: Partial<CreatePortfolioRequest>): Promise<Portfolio> => {
    const res = await apiClient.put<ApiResponse<Portfolio>>(`/portfolios/${id}`, data);
    return res.data.data;
  },

  deletePortfolio: async (id: number): Promise<void> => {
    await apiClient.delete(`/portfolios/${id}`);
  },

  getValuation: async (id: number): Promise<PortfolioValuation> => {
    const res = await apiClient.get<ApiResponse<PortfolioValuation>>(`/portfolios/${id}/valuation`);
    return res.data.data;
  },

  // Holdings
  getHoldings: async (portfolioId: number): Promise<Holding[]> => {
    const res = await apiClient.get<ApiResponse<Holding[]>>(`/portfolios/${portfolioId}/holdings`);
    return res.data.data;
  },

  addHolding: async (portfolioId: number, data: CreateHoldingRequest): Promise<Holding> => {
    const cost = data.averageCost ?? data.costBasis ?? 0;
    const res = await apiClient.post<ApiResponse<Holding>>(`/portfolios/${portfolioId}/holdings`, {
      ...data,
      symbol: data.symbol?.trim().toUpperCase(),
      averageCost: cost,
      costBasis: cost,
    });
    return res.data.data;
  },

  updateHolding: async (
    portfolioId: number,
    holdingId: number,
    data: UpdateHoldingRequest
  ): Promise<Holding> => {
    const res = await apiClient.put<ApiResponse<Holding>>(
      `/portfolios/${portfolioId}/holdings/${holdingId}`,
      data
    );
    return res.data.data;
  },

  deleteHolding: async (portfolioId: number, holdingId: number): Promise<void> => {
    await apiClient.delete(`/portfolios/${portfolioId}/holdings/${holdingId}`);
  },

  // Transactions
  getTransactions: async (
    portfolioId: number,
    page: number = 0,
    size: number = 20
  ): Promise<PagedResponse<Transaction>> => {
    const res = await apiClient.get<ApiResponse<PagedResponse<Transaction>>>(
      `/portfolios/${portfolioId}/transactions?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  createTransaction: async (
    portfolioId: number,
    data: CreateTransactionRequest
  ): Promise<Transaction> => {
    let formattedDate = data.transactionDate;
    if (formattedDate) {
      if (!formattedDate.includes('T')) {
        formattedDate = `${formattedDate}T00:00:00Z`;
      }
    } else {
      formattedDate = new Date().toISOString();
    }

    const res = await apiClient.post<ApiResponse<Transaction>>(
      `/portfolios/${portfolioId}/transactions`,
      {
        ...data,
        symbol: data.symbol?.trim().toUpperCase(),
        transactionDate: formattedDate,
      }
    );
    return res.data.data;
  },
};
