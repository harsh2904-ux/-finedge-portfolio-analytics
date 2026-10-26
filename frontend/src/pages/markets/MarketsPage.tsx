import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  TrendingUp,
  Search,
  RefreshCw,
  SlidersHorizontal,
  DollarSign,
  BarChart2,
  Calendar,
  Layers,
} from 'lucide-react';
import { marketApi, assetApi } from '../../api/marketApi';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Input } from '../../components/common/Input';
import { Modal } from '../../components/common/Modal';
import { MarketCandleChart } from '../../components/charts/MarketCandleChart';
import { formatCurrency } from '../../utils/formatters';
import type { Asset } from '../../types/asset';
import type { PriceBar } from '../../types/market';

export const MarketsPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedAssetClass, setSelectedAssetClass] = useState<string>('ALL');
  const [selectedAssetForChart, setSelectedAssetForChart] = useState<Asset | null>(null);

  const DEFAULT_PRICES: Record<string, number> = {
    RELIANCE: 3287.78,
    TCS: 4210.0,
    HDFCBANK: 1650.0,
    INFY: 1890.25,
    ICICIBANK: 1240.75,
    TATAMOTORS: 980.0,
    ITC: 510.3,
    BHARTIARTL: 1680.0,
    LT: 3620.0,
    NIFTYBEES: 275.4,
    GOLDBEES: 68.2,
  };

  // Fetch all assets
  const { data: rawAssets = [], isLoading, refetch } = useQuery({
    queryKey: ['assets'],
    queryFn: () => assetApi.getAssets(),
  });

  const assets: Asset[] = Array.isArray(rawAssets) ? rawAssets : [];

  // Fetch price history for modal if asset selected
  const { data: priceHistory, isLoading: chartLoading } = useQuery({
    queryKey: ['price-history', selectedAssetForChart?.symbol],
    queryFn: async () => {
      const res = await marketApi.getHistory(selectedAssetForChart!.symbol);
      return res?.data || [];
    },
    enabled: !!selectedAssetForChart,
  });

  const priceBars: PriceBar[] = Array.isArray(priceHistory) ? priceHistory : [];

  const assetClasses = ['ALL', 'EQUITY', 'ETF', 'COMMODITY', 'INDEX'];

  const filteredAssets = assets.filter((asset) => {
    const matchesSearch =
      asset.symbol.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (asset.name && asset.name.toLowerCase().includes(searchTerm.toLowerCase()));
    const matchesClass =
      selectedAssetClass === 'ALL' ||
      (asset.assetClass && asset.assetClass.toUpperCase().includes(selectedAssetClass));
    return matchesSearch && matchesClass;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <TrendingUp className="w-5 h-5 text-fin-blue" />
            Live Market Watch (Indian Bluechips & ETFs)
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Live prices, daily candlestick charts, and market data for top Indian companies in INR (₹)
          </p>
        </div>

        <Button
          variant="secondary"
          size="sm"
          onClick={() => refetch()}
          leftIcon={<RefreshCw className="w-3.5 h-3.5" />}
        >
          Refresh Feed
        </Button>
      </div>

      {/* Info Banner */}
      <div className="p-4 rounded-xl bg-fin-card border border-fin-border flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-xs">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-lg bg-fin-emerald/10 flex items-center justify-center text-fin-emerald font-bold">
            NSE
          </div>
          <div>
            <div className="font-semibold text-white">National Stock Exchange (NSE) Active Feed</div>
            <div className="text-fin-muted">
              Live quotes and historical candlestick data for top Indian bluechips and benchmark ETFs. Click any row to view candlestick chart.
            </div>
          </div>
        </div>
        <Badge variant="success" size="sm">
          Currency: INR (₹)
        </Badge>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col md:flex-row items-center justify-between gap-4 bg-fin-card p-4 rounded-xl border border-fin-border">
        <div className="w-full md:w-80">
          <Input
            placeholder="Search by symbol or name (e.g. RELIANCE, TCS, NIFTYBEES)..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            leftIcon={<Search className="w-4 h-4" />}
          />
        </div>

        {/* Filter Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto w-full md:w-auto scrollbar-none">
          {assetClasses.map((ac) => (
            <button
              key={ac}
              onClick={() => setSelectedAssetClass(ac)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                selectedAssetClass === ac
                  ? 'bg-fin-blue text-white shadow-sm'
                  : 'bg-fin-panel text-fin-muted hover:text-fin-text border border-fin-border'
              }`}
            >
              {ac}
            </button>
          ))}
        </div>
      </div>

      {/* Asset Table */}
      <Card noPadding>
        <div className="overflow-x-auto">
          <table className="w-full min-w-[680px] text-left border-collapse">
            <thead>
              <tr className="border-b border-fin-border bg-fin-panel/50 text-[11px] font-mono uppercase tracking-wider text-fin-muted">
                <th className="px-5 py-3 font-semibold">Instrument</th>
                <th className="px-5 py-3 font-semibold">Asset Class</th>
                <th className="px-5 py-3 font-semibold">Exchange</th>
                <th className="px-5 py-3 font-semibold">Currency</th>
                <th className="px-5 py-3 font-semibold text-right">Latest Close</th>
                <th className="px-5 py-3 font-semibold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-fin-border/50 text-xs">
              {isLoading ? (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-fin-muted">
                    Loading asset instruments...
                  </td>
                </tr>
              ) : filteredAssets.length === 0 ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-fin-muted">
                    No instruments matching your search criteria.
                  </td>
                </tr>
              ) : (
                filteredAssets.map((asset) => (
                  <tr
                    key={asset.symbol}
                    className="hover:bg-fin-panel/40 transition-colors cursor-pointer"
                    onClick={() => setSelectedAssetForChart(asset)}
                  >
                    <td className="px-5 py-3 font-mono font-bold text-white">
                      <div className="flex flex-col">
                        <span className="text-sm font-semibold">{asset.symbol}</span>
                        <span className="text-[11px] font-normal text-fin-muted font-sans truncate max-w-xs">
                          {asset.name || asset.symbol}
                        </span>
                      </div>
                    </td>
                    <td className="px-5 py-3">
                      <Badge variant="info" size="sm">
                        {asset.assetClass || 'EQUITY'}
                      </Badge>
                    </td>
                    <td className="px-5 py-3 font-mono text-fin-muted">
                      {asset.exchange || 'NSE'}
                    </td>
                    <td className="px-5 py-3 font-mono text-fin-muted">
                      {asset.currency || 'INR'}
                    </td>
                    <td className="px-5 py-3 font-mono text-right font-semibold text-white">
                      {formatCurrency(asset.latestClose || DEFAULT_PRICES[asset.symbol] || 0, asset.currency || 'INR')}
                    </td>
                    <td className="px-5 py-3 text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={(e) => {
                          e.stopPropagation();
                          setSelectedAssetForChart(asset);
                        }}
                        leftIcon={<BarChart2 className="w-3.5 h-3.5" />}
                      >
                        Chart
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Asset Candlestick Modal */}
      <Modal
        isOpen={!!selectedAssetForChart}
        onClose={() => setSelectedAssetForChart(null)}
        title={selectedAssetForChart ? `${selectedAssetForChart.symbol} - ${selectedAssetForChart.name}` : ''}
        subtitle="1-Year Historical Daily Candlesticks (OHLC) & Trading Volume"
        maxWidth="2xl"
      >
        {selectedAssetForChart && (
          <div className="space-y-4">
            <MarketCandleChart
              symbol={selectedAssetForChart.symbol}
              data={priceBars}
              isLoading={chartLoading}
              height={400}
            />
          </div>
        )}
      </Modal>
    </div>
  );
};
