export interface Asset {
  id: number;
  symbol: string;
  providerSymbol: string;
  name: string;
  assetClass: string;
  sector?: string;
  currency: string;
  exchange?: string;
  country?: string;
  active: boolean;
  latestClose?: number;
  createdAt?: string;
}

export interface CreateAssetRequest {
  symbol: string;
  providerSymbol?: string;
  name: string;
  assetClass: string;
  sector?: string;
  currency?: string;
  exchange?: string;
  country?: string;
}
