export interface LatestPriceResponse {
  symbol: string;
  name: string;
  currency: string;
  price: number;
  change: number;
  changePercent: number;
  asOf: string;
}

export interface MarketPricePoint {
  timestamp: string;
  date?: string;
  open: number;
  high: number;
  low: number;
  close: number;
  adjustedClose?: number;
  volume: number;
}

export type PriceBar = MarketPricePoint;

export interface MarketHistoryResponse {
  symbol: string;
  currency: string;
  totalRecords: number;
  data: MarketPricePoint[];
}
