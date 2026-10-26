export type TransactionType = 'BUY' | 'SELL' | 'DIVIDEND' | 'FEE';

export interface Portfolio {
  id: number;
  userId: number;
  name: string;
  description?: string;
  baseCurrency: string;
  benchmarkAssetId?: number;
  benchmarkSymbol?: string;
  holdingsCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePortfolioRequest {
  name: string;
  description?: string;
  baseCurrency?: string;
  benchmarkSymbol?: string;
}

export interface PositionValuation {
  holdingId?: number;
  assetId: number;
  symbol: string;
  name: string;
  assetClass: string;
  quantity: number;
  averageCost: number;
  currentPrice: number;
  marketValue: number;
  currentValue?: number;
  weight: number;
  costBasis: number;
  unrealizedPnL: number;
  unrealizedGainLoss?: number;
  unrealizedPnLPercent: number;
  unrealizedGainLossPercent?: number;
  dailyReturn?: number;
}

export interface PortfolioValuation {
  portfolioId: number;
  portfolioName: string;
  portfolioValue: number;
  totalValue?: number;
  dailyPnL: number;
  dailyReturn: number;
  totalCostBasis: number;
  totalUnrealizedPnL: number;
  unrealizedGainLoss?: number;
  totalUnrealizedPnLPercent: number;
  unrealizedGainLossPercent?: number;
  currency: string;
  baseCurrency?: string;
  asOf: string;
  asOfDate?: string;
  positions: PositionValuation[];
  holdings?: PositionValuation[];
}

export interface Holding {
  id: number;
  portfolioId: number;
  assetId: number;
  symbol: string;
  assetName: string;
  assetClass: string;
  currency: string;
  quantity: number;
  averageCost: number;
  costBasis?: number;
  targetWeight?: number;
  purchaseDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateHoldingRequest {
  symbol: string;
  quantity: number;
  averageCost?: number;
  costBasis?: number;
  targetWeight?: number;
  purchaseDate?: string;
}

export interface UpdateHoldingRequest {
  quantity?: number;
  averageCost?: number;
  costBasis?: number;
  targetWeight?: number;
}

export interface Transaction {
  id: number;
  portfolioId: number;
  assetId: number;
  symbol: string;
  assetName: string;
  transactionType: TransactionType;
  quantity: number;
  price: number;
  totalAmount: number;
  transactionCost?: number;
  fees?: number;
  currency: string;
  transactionDate?: string;
  notes?: string;
  createdAt: string;
}

export interface CreateTransactionRequest {
  symbol: string;
  transactionType: TransactionType;
  type?: TransactionType;
  quantity: number;
  price: number;
  transactionCost?: number;
  fees?: number;
  currency?: string;
  transactionDate?: string;
  notes?: string;
}
