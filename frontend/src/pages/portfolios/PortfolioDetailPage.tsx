import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Briefcase,
  Plus,
  ArrowLeft,
  Trash2,
  Edit2,
  DollarSign,
  TrendingUp,
  Clock,
  ArrowDownRight,
  ArrowUpRight,
  Sliders,
  Dice5,
} from 'lucide-react';
import { portfolioApi } from '../../api/portfolioApi';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Modal } from '../../components/common/Modal';
import { Input } from '../../components/common/Input';
import { Select } from '../../components/common/Select';
import { Tabs } from '../../components/common/Tabs';
import { formatCurrency, formatPercent } from '../../utils/formatters';
import type { Holding, Transaction } from '../../types/portfolio';

const addHoldingSchema = z.object({
  symbol: z.string().min(1, 'Symbol is required').toUpperCase(),
  quantity: z.number().positive('Quantity must be greater than 0'),
  costBasis: z.number().positive('Cost basis must be greater than 0'),
});

const addTransactionSchema = z.object({
  symbol: z.string().min(1, 'Symbol is required').toUpperCase(),
  transactionType: z.enum(['BUY', 'SELL']),
  quantity: z.number().positive('Quantity must be greater than 0'),
  price: z.number().positive('Price must be greater than 0'),
  fees: z.number().min(0).optional(),
  transactionDate: z.string().min(1, 'Date is required'),
  notes: z.string().optional(),
});

type AddHoldingForm = z.infer<typeof addHoldingSchema>;
type AddTransactionForm = z.infer<typeof addTransactionSchema>;

const POPULAR_NSE_STOCKS = [
  { symbol: 'RELIANCE', name: 'Reliance Ind.', price: 2980.5 },
  { symbol: 'TCS', name: 'TCS', price: 4210.0 },
  { symbol: 'HDFCBANK', name: 'HDFC Bank', price: 1650.0 },
  { symbol: 'INFY', name: 'Infosys', price: 1890.25 },
  { symbol: 'ICICIBANK', name: 'ICICI Bank', price: 1240.75 },
  { symbol: 'TATAMOTORS', name: 'Tata Motors', price: 980.0 },
  { symbol: 'ITC', name: 'ITC Ltd', price: 510.3 },
  { symbol: 'BHARTIARTL', name: 'Bharti Airtel', price: 1680.0 },
  { symbol: 'LT', name: 'Larsen & Toubro', price: 3620.0 },
  { symbol: 'NIFTYBEES', name: 'Nifty 50 ETF', price: 275.4 },
  { symbol: 'GOLDBEES', name: 'Gold BeES ETF', price: 68.2 },
];

export const PortfolioDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const portfolioId = Number(id);
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [activeTab, setActiveTab] = useState<'holdings' | 'transactions'>('holdings');
  const [isAddHoldingOpen, setIsAddHoldingOpen] = useState(false);
  const [isAddTradeOpen, setIsAddTradeOpen] = useState(false);
  const [editingHolding, setEditingHolding] = useState<Holding | null>(null);

  // Queries
  const { data: portfolio, isLoading: portLoading } = useQuery({
    queryKey: ['portfolio', portfolioId],
    queryFn: () => portfolioApi.getPortfolio(portfolioId),
    enabled: !!portfolioId,
  });

  const { data: valuation, isLoading: valLoading } = useQuery({
    queryKey: ['valuation', portfolioId],
    queryFn: () => portfolioApi.getValuation(portfolioId),
    enabled: !!portfolioId,
  });

  const { data: holdings = [], isLoading: holdLoading } = useQuery({
    queryKey: ['holdings', portfolioId],
    queryFn: () => portfolioApi.getHoldings(portfolioId),
    enabled: !!portfolioId,
  });

  const { data: transactionsPage, isLoading: txLoading } = useQuery({
    queryKey: ['transactions', portfolioId],
    queryFn: () => portfolioApi.getTransactions(portfolioId, 0, 50),
    enabled: !!portfolioId,
  });

  // Holding Forms
  const {
    register: regHolding,
    handleSubmit: handleHoldingSubmit,
    reset: resetHolding,
    setValue: setHoldingValue,
    watch: watchHolding,
    formState: { errors: holdingErrors },
  } = useForm<AddHoldingForm>({
    resolver: zodResolver(addHoldingSchema),
  });

  // Transaction Forms
  const {
    register: regTrade,
    handleSubmit: handleTradeSubmit,
    reset: resetTrade,
    setValue: setTradeValue,
    watch: watchTrade,
    formState: { errors: tradeErrors },
  } = useForm<AddTransactionForm>({
    resolver: zodResolver(addTransactionSchema),
    defaultValues: {
      transactionType: 'BUY',
      fees: 0,
      transactionDate: new Date().toISOString().split('T')[0],
    },
  });

  // Mutations
  const addHoldingMutation = useMutation({
    mutationFn: async (data: AddHoldingForm) => {
      const sym = data.symbol.trim().toUpperCase();
      const existing = (holdings || []).find((h) => h.symbol.toUpperCase() === sym);
      if (existing) {
        return portfolioApi.createTransaction(portfolioId, {
          symbol: sym,
          transactionType: 'BUY',
          quantity: data.quantity,
          price: data.costBasis,
          transactionCost: 0,
          transactionDate: new Date().toISOString().split('T')[0],
          notes: 'Added shares via portfolio manager',
        });
      }
      return portfolioApi.addHolding(portfolioId, {
        symbol: sym,
        quantity: data.quantity,
        averageCost: data.costBasis,
        costBasis: data.costBasis,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['holdings', portfolioId] });
      queryClient.invalidateQueries({ queryKey: ['valuation', portfolioId] });
      queryClient.invalidateQueries({ queryKey: ['transactions', portfolioId] });
      setIsAddHoldingOpen(false);
      resetHolding();
    },
  });

  const deleteHoldingMutation = useMutation({
    mutationFn: (holdingId: number) => portfolioApi.deleteHolding(portfolioId, holdingId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['holdings', portfolioId] });
      queryClient.invalidateQueries({ queryKey: ['valuation', portfolioId] });
    },
  });

  const addTradeMutation = useMutation({
    mutationFn: (data: AddTransactionForm) => {
      const formattedDate = data.transactionDate
        ? (data.transactionDate.includes('T') ? data.transactionDate : `${data.transactionDate}T00:00:00Z`)
        : new Date().toISOString();

      return portfolioApi.createTransaction(portfolioId, {
        symbol: data.symbol.trim().toUpperCase(),
        transactionType: data.transactionType,
        quantity: data.quantity,
        price: data.price,
        transactionCost: data.fees || 0,
        transactionDate: formattedDate,
        notes: data.notes,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transactions', portfolioId] });
      queryClient.invalidateQueries({ queryKey: ['holdings', portfolioId] });
      queryClient.invalidateQueries({ queryKey: ['valuation', portfolioId] });
      setIsAddTradeOpen(false);
      resetTrade();
    },
  });

  const onAddHolding = (data: AddHoldingForm) => {
    addHoldingMutation.mutate(data);
  };

  const onAddTrade = (data: AddTransactionForm) => {
    addTradeMutation.mutate(data);
  };

  const handleDeleteHolding = (holdingId: number, symbol: string) => {
    if (confirm(`Remove holding ${symbol}?`)) {
      deleteHoldingMutation.mutate(holdingId);
    }
  };

  const transactions = transactionsPage?.content || [];

  return (
    <div className="space-y-6">
      {/* Back button & Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <button
            onClick={() => navigate('/portfolios')}
            className="inline-flex items-center gap-1.5 text-xs text-fin-muted hover:text-fin-text mb-2 transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Back to All Portfolios</span>
          </button>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-3">
            {portfolio?.name || 'Loading Portfolio...'}
            {portfolio && (
              <Badge variant="info" size="sm">
                {portfolio.baseCurrency}
              </Badge>
            )}
            {portfolio?.benchmarkSymbol && (
              <Badge variant="default" size="sm">
                Benchmark: {portfolio.benchmarkSymbol}
              </Badge>
            )}
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            {portfolio?.description || 'Active investment portfolio'}
          </p>
        </div>

        <div className="flex items-center gap-2 flex-wrap">
          <Button
            variant="outline"
            size="sm"
            onClick={() => navigate('/optimization')}
            leftIcon={<Sliders className="w-3.5 h-3.5" />}
          >
            Optimize
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => navigate('/simulation')}
            leftIcon={<Dice5 className="w-3.5 h-3.5" />}
          >
            Simulate
          </Button>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => setIsAddTradeOpen(true)}
            leftIcon={<TrendingUp className="w-3.5 h-3.5" />}
          >
            Record Trade
          </Button>
          <Button
            variant="primary"
            size="sm"
            onClick={() => setIsAddHoldingOpen(true)}
            leftIcon={<Plus className="w-3.5 h-3.5" />}
          >
            Add Holding
          </Button>
        </div>
      </div>

      {/* Mini KPI banner */}
      {valuation && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 p-4 rounded-xl bg-fin-card border border-fin-border">
          <div>
            <div className="text-[11px] font-mono uppercase text-fin-muted">Market Value</div>
            <div className="text-lg font-bold font-mono text-white">
              {formatCurrency(valuation.totalValue, valuation.baseCurrency)}
            </div>
          </div>
          <div>
            <div className="text-[11px] font-mono uppercase text-fin-muted">Unrealized P&L</div>
            <div
              className={`text-lg font-bold font-mono ${
                (valuation.totalUnrealizedPnL ?? valuation.unrealizedGainLoss ?? 0) >= 0 ? 'text-fin-emerald' : 'text-fin-red'
              }`}
            >
              {(valuation.totalUnrealizedPnL ?? valuation.unrealizedGainLoss ?? 0) >= 0 ? '+' : ''}
              {formatCurrency(valuation.totalUnrealizedPnL ?? valuation.unrealizedGainLoss ?? 0, valuation.currency || valuation.baseCurrency || 'INR')} (
              {(((valuation.totalUnrealizedPnLPercent ?? valuation.unrealizedGainLossPercent ?? 0)) * 100).toFixed(2)}%)
            </div>
          </div>
          <div>
            <div className="text-[11px] font-mono uppercase text-fin-muted">Total Positions</div>
            <div className="text-lg font-bold font-mono text-white">
              {valuation.holdings?.length || 0} Assets
            </div>
          </div>
          <div>
            <div className="text-[11px] font-mono uppercase text-fin-muted">Valuation As Of</div>
            <div className="text-xs font-mono text-fin-muted mt-1">{valuation.asOfDate || 'Live'}</div>
          </div>
        </div>
      )}

      {/* Tabs */}
      <Tabs
        tabs={[
          { id: 'holdings', label: 'Positions & Holdings', badge: holdings.length },
          { id: 'transactions', label: 'Transaction Ledger', badge: transactions.length },
        ]}
        activeTab={activeTab}
        onChange={(tabId) => setActiveTab(tabId as any)}
      />

      {/* Tab 1: Holdings */}
      {activeTab === 'holdings' && (
        <Card noPadding>
          <div className="overflow-x-auto">
            <table className="w-full min-w-[760px] text-left border-collapse">
              <thead>
                <tr className="border-b border-fin-border bg-fin-panel/50 text-[11px] font-mono uppercase tracking-wider text-fin-muted">
                  <th className="px-5 py-3 font-semibold">Symbol</th>
                  <th className="px-5 py-3 font-semibold text-right">Shares</th>
                  <th className="px-5 py-3 font-semibold text-right">Avg Cost</th>
                  <th className="px-5 py-3 font-semibold text-right">Current Price</th>
                  <th className="px-5 py-3 font-semibold text-right">Current Value</th>
                  <th className="px-5 py-3 font-semibold text-right">Weight</th>
                  <th className="px-5 py-3 font-semibold text-right">Unrealized P&L</th>
                  <th className="px-5 py-3 font-semibold text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-fin-border/50 text-xs">
                {holdLoading ? (
                  <tr>
                    <td colSpan={8} className="py-8 text-center text-fin-muted">
                      Loading holdings...
                    </td>
                  </tr>
                ) : holdings.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="py-12 text-center text-fin-muted">
                      No positions recorded yet. Click "Add Holding" to record your first asset.
                    </td>
                  </tr>
                ) : (
                  holdings.map((h) => {
                    const valHolding = (valuation?.positions || valuation?.holdings)?.find((vh) => vh.symbol === h.symbol);
                    const costBasis = h.averageCost || h.costBasis || 0;
                    const currentPrice = valHolding ? valHolding.currentPrice : costBasis;
                    const currentValue = valHolding ? (valHolding.marketValue || valHolding.currentValue || 0) : h.quantity * costBasis;
                    const weight = valHolding ? valHolding.weight : 0;
                    const pnl = valHolding ? (valHolding.unrealizedPnL ?? valHolding.unrealizedGainLoss ?? 0) : 0;
                    const pnlPct = valHolding ? (valHolding.unrealizedPnLPercent ?? valHolding.unrealizedGainLossPercent ?? 0) : 0;

                    return (
                      <tr key={h.id} className="hover:bg-fin-panel/40 transition-colors">
                        <td className="px-5 py-3 font-mono font-bold text-white">
                          <span className="text-sm">{h.symbol}</span>
                        </td>
                        <td className="px-5 py-3 font-mono text-right text-fin-text">
                          {h.quantity.toLocaleString()}
                        </td>
                        <td className="px-5 py-3 font-mono text-right text-fin-muted">
                          {formatCurrency(costBasis, portfolio?.baseCurrency || 'INR')}
                        </td>
                        <td className="px-5 py-3 font-mono text-right text-white font-medium">
                          {formatCurrency(currentPrice, portfolio?.baseCurrency || 'INR')}
                        </td>
                        <td className="px-5 py-3 font-mono text-right text-white font-semibold">
                          {formatCurrency(currentValue, portfolio?.baseCurrency || 'INR')}
                        </td>
                        <td className="px-5 py-3 font-mono text-right text-fin-blue font-medium">
                          {(weight * 100).toFixed(2)}%
                        </td>
                        <td className="px-5 py-3 font-mono text-right">
                          <span
                            className={`font-semibold ${
                              pnl >= 0 ? 'text-fin-emerald' : 'text-fin-red'
                            }`}
                          >
                            {pnl >= 0 ? '+' : ''}
                            {formatCurrency(pnl, portfolio?.baseCurrency || 'INR')} (
                            {(pnlPct * 100).toFixed(2)}%)
                          </span>
                        </td>
                        <td className="px-5 py-3 text-right">
                          <div className="flex items-center justify-end gap-1.5">
                            <button
                              onClick={() => {
                                setTradeValue('transactionType', 'BUY');
                                setTradeValue('symbol', h.symbol);
                                setTradeValue('price', currentPrice);
                                setTradeValue('quantity', 10);
                                setIsAddTradeOpen(true);
                              }}
                              className="px-2 py-1 rounded bg-fin-panel border border-fin-border hover:border-fin-emerald text-[11px] font-semibold text-fin-emerald hover:bg-emerald-500/10 transition-colors"
                              title={`Buy more shares of ${h.symbol}`}
                            >
                              + Buy
                            </button>
                            <button
                              onClick={() => {
                                setTradeValue('transactionType', 'SELL');
                                setTradeValue('symbol', h.symbol);
                                setTradeValue('price', currentPrice);
                                setTradeValue('quantity', Math.min(10, h.quantity));
                                setIsAddTradeOpen(true);
                              }}
                              className="px-2 py-1 rounded bg-fin-panel border border-fin-border hover:border-fin-red text-[11px] font-semibold text-fin-red hover:bg-red-500/10 transition-colors"
                              title={`Sell shares of ${h.symbol}`}
                            >
                              - Sell
                            </button>
                            <button
                              onClick={() => handleDeleteHolding(h.id, h.symbol)}
                              className="p-1 rounded text-fin-muted hover:text-fin-red hover:bg-fin-panel transition-colors"
                              title="Remove Position"
                            >
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* Tab 2: Transactions */}
      {activeTab === 'transactions' && (
        <Card noPadding>
          <div className="overflow-x-auto">
            <table className="w-full min-w-[760px] text-left border-collapse">
              <thead>
                <tr className="border-b border-fin-border bg-fin-panel/50 text-[11px] font-mono uppercase tracking-wider text-fin-muted">
                  <th className="px-5 py-3 font-semibold">Date</th>
                  <th className="px-5 py-3 font-semibold">Type</th>
                  <th className="px-5 py-3 font-semibold">Asset</th>
                  <th className="px-5 py-3 font-semibold text-right">Quantity</th>
                  <th className="px-5 py-3 font-semibold text-right">Price</th>
                  <th className="px-5 py-3 font-semibold text-right">Total Amount</th>
                  <th className="px-5 py-3 font-semibold text-right">Fees</th>
                  <th className="px-5 py-3 font-semibold">Notes</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-fin-border/50 text-xs">
                {txLoading ? (
                  <tr>
                    <td colSpan={8} className="py-8 text-center text-fin-muted">
                      Loading transaction ledger...
                    </td>
                  </tr>
                ) : transactions.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="py-12 text-center text-fin-muted">
                      No transactions recorded yet. Click "Record Trade" to log buy and sell activity.
                    </td>
                  </tr>
                ) : (
                  transactions.map((tx) => (
                    <tr key={tx.id} className="hover:bg-fin-panel/40 transition-colors">
                      <td className="px-5 py-3 font-mono text-fin-muted">
                        {tx.transactionDate ? new Date(tx.transactionDate).toLocaleDateString() : '-'}
                      </td>
                      <td className="px-5 py-3">
                        <Badge
                          variant={tx.transactionType === 'BUY' ? 'success' : 'danger'}
                          size="sm"
                        >
                          {tx.transactionType}
                        </Badge>
                      </td>
                      <td className="px-5 py-3 font-mono font-bold text-white">{tx.symbol}</td>
                      <td className="px-5 py-3 font-mono text-right text-fin-text">
                        {tx.quantity.toLocaleString()}
                      </td>
                      <td className="px-5 py-3 font-mono text-right text-white">
                        {formatCurrency(tx.price, portfolio?.baseCurrency || 'INR')}
                      </td>
                      <td className="px-5 py-3 font-mono text-right font-semibold text-white">
                        {formatCurrency(tx.totalAmount, portfolio?.baseCurrency || 'INR')}
                      </td>
                      <td className="px-5 py-3 font-mono text-right text-fin-muted">
                        {formatCurrency(tx.fees, portfolio?.baseCurrency || 'INR')}
                      </td>
                      <td className="px-5 py-3 text-fin-muted truncate max-w-xs">{tx.notes || '-'}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* Add Holding Modal */}
      <Modal
        isOpen={isAddHoldingOpen}
        onClose={() => setIsAddHoldingOpen(false)}
        title="Add Asset Holding"
        subtitle="Add a new stock or ETF position to this portfolio"
      >
        <form onSubmit={handleHoldingSubmit(onAddHolding)} className="space-y-4">
          {addHoldingMutation.isError && (
            <div className="p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-xs text-fin-red">
              {(addHoldingMutation.error as any)?.response?.data?.error?.message ||
                (addHoldingMutation.error as any)?.response?.data?.message ||
                'Failed to add position. Please verify the stock symbol (e.g. RELIANCE, TCS, INFY, ITC, LT).'}
            </div>
          )}

          {(holdings || []).some((h) => h.symbol.toUpperCase() === (watchHolding('symbol') || '').trim().toUpperCase()) && (
            <div className="p-2.5 rounded-lg bg-fin-blue/10 border border-fin-blue/30 text-xs text-fin-blue flex items-center gap-2">
              <span className="font-semibold">💡 Note:</span>
              <span>You already own this stock. Adding here will add more shares and update your average buy price.</span>
            </div>
          )}

          {/* Quick Stock Selector Pills */}
          <div>
            <label className="block text-xs font-medium text-fin-text mb-1.5">
              Quick Select Stock / ETF:
            </label>
            <div className="flex flex-wrap gap-1.5 p-2 rounded-lg bg-fin-panel border border-fin-border">
              {POPULAR_NSE_STOCKS.map((stock) => (
                <button
                  key={stock.symbol}
                  type="button"
                  onClick={() => {
                    setHoldingValue('symbol', stock.symbol);
                    setHoldingValue('costBasis', stock.price);
                  }}
                  className="px-2 py-1 text-[11px] rounded bg-fin-card border border-fin-border hover:border-fin-blue text-fin-text hover:text-white transition-all font-mono"
                  title={`${stock.name} - ₹${stock.price}`}
                >
                  {stock.symbol}
                </button>
              ))}
            </div>
          </div>

          <Input
            label="Asset Symbol"
            placeholder="e.g. RELIANCE, TCS, HDFCBANK, INFY, NIFTYBEES"
            error={holdingErrors.symbol?.message}
            {...regHolding('symbol')}
          />

          <div className="grid grid-cols-2 gap-4">
            <div>
              <Input
                label="Quantity / Shares"
                type="number"
                step="any"
                placeholder="e.g. 50"
                error={holdingErrors.quantity?.message}
                {...regHolding('quantity', { valueAsNumber: true })}
              />
              <div className="flex gap-1.5 mt-1.5 items-center">
                <span className="text-[10px] text-fin-muted">Presets:</span>
                {[10, 25, 50, 100, 250].map((qty) => (
                  <button
                    key={qty}
                    type="button"
                    onClick={() => setHoldingValue('quantity', qty)}
                    className="px-1.5 py-0.5 rounded bg-fin-panel border border-fin-border hover:border-fin-blue text-[10px] text-fin-muted hover:text-white font-mono"
                  >
                    {qty}
                  </button>
                ))}
              </div>
            </div>

            <Input
              label="Average Cost Basis (₹ Per Share)"
              type="number"
              step="any"
              placeholder="e.g. 2980.50"
              error={holdingErrors.costBasis?.message}
              {...regHolding('costBasis', { valueAsNumber: true })}
            />
          </div>

          {/* Dynamic Investment Calculator */}
          {watchHolding('quantity') && watchHolding('costBasis') && Number(watchHolding('quantity')) > 0 && Number(watchHolding('costBasis')) > 0 ? (
            <div className="p-3 rounded-lg bg-fin-panel border border-fin-border flex justify-between items-center text-xs">
              <span className="text-fin-muted">Estimated Total Investment:</span>
              <span className="font-mono font-bold text-fin-emerald text-sm">
                {formatCurrency(Number(watchHolding('quantity')) * Number(watchHolding('costBasis')), portfolio?.baseCurrency || 'INR')}
              </span>
            </div>
          ) : null}

          <div className="pt-4 flex items-center justify-end gap-3">
            <Button type="button" variant="outline" onClick={() => setIsAddHoldingOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" isLoading={addHoldingMutation.isPending}>
              Add Position
            </Button>
          </div>
        </form>
      </Modal>

      {/* Record Trade Modal */}
      <Modal
        isOpen={isAddTradeOpen}
        onClose={() => setIsAddTradeOpen(false)}
        title="Record Trade Execution"
        subtitle="Log a buy or sell transaction into the portfolio ledger"
      >
        <form onSubmit={handleTradeSubmit(onAddTrade)} className="space-y-4">
          {addTradeMutation.isError && (
            <div className="p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-xs text-fin-red">
              {(addTradeMutation.error as any)?.response?.data?.error?.message ||
                (addTradeMutation.error as any)?.response?.data?.message ||
                'Failed to record trade. Please check your price, quantity, and asset symbol.'}
            </div>
          )}

          {/* Quick Stock Selector Pills */}
          <div>
            <label className="block text-xs font-medium text-fin-text mb-1.5">
              Quick Select Asset:
            </label>
            <div className="flex flex-wrap gap-1.5 p-2 rounded-lg bg-fin-panel border border-fin-border">
              {POPULAR_NSE_STOCKS.map((stock) => (
                <button
                  key={stock.symbol}
                  type="button"
                  onClick={() => {
                    setTradeValue('symbol', stock.symbol);
                    setTradeValue('price', stock.price);
                  }}
                  className="px-2 py-1 text-[11px] rounded bg-fin-card border border-fin-border hover:border-fin-blue text-fin-text hover:text-white transition-all font-mono"
                  title={`${stock.name} - ₹${stock.price}`}
                >
                  {stock.symbol}
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Transaction Type"
              options={[
                { value: 'BUY', label: 'BUY (Acquisition)' },
                { value: 'SELL', label: 'SELL (Liquidation)' },
              ]}
              {...regTrade('transactionType')}
            />

            <Input
              label="Asset Symbol"
              placeholder="e.g. RELIANCE"
              error={tradeErrors.symbol?.message}
              {...regTrade('symbol')}
            />
          </div>

          {watchTrade('transactionType') === 'SELL' && watchTrade('symbol') && (
            <div className="p-2.5 rounded-lg bg-fin-panel border border-fin-border text-xs flex justify-between items-center">
              <span className="text-fin-muted">Shares currently owned:</span>
              <span className="font-mono font-bold text-white">
                {(holdings || []).find((h) => h.symbol.toUpperCase() === (watchTrade('symbol') || '').trim().toUpperCase())?.quantity ?? 0} shares
              </span>
            </div>
          )}

          <div className="grid grid-cols-2 gap-4">
            <div>
              <Input
                label="Quantity"
                type="number"
                step="any"
                placeholder="e.g. 25"
                error={tradeErrors.quantity?.message}
                {...regTrade('quantity', { valueAsNumber: true })}
              />
              <div className="flex gap-1.5 mt-1.5 items-center">
                <span className="text-[10px] text-fin-muted">Presets:</span>
                {[10, 25, 50, 100, 250].map((qty) => (
                  <button
                    key={qty}
                    type="button"
                    onClick={() => setTradeValue('quantity', qty)}
                    className="px-1.5 py-0.5 rounded bg-fin-panel border border-fin-border hover:border-fin-blue text-[10px] text-fin-muted hover:text-white font-mono"
                  >
                    {qty}
                  </button>
                ))}
              </div>
            </div>

            <Input
              label="Execution Price (₹)"
              type="number"
              step="any"
              placeholder="e.g. 2980.50"
              error={tradeErrors.price?.message}
              {...regTrade('price', { valueAsNumber: true })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Brokerage / STT Fees (₹)"
              type="number"
              step="any"
              placeholder="0.00"
              {...regTrade('fees', { valueAsNumber: true })}
            />

            <Input
              label="Execution Date"
              type="date"
              error={tradeErrors.transactionDate?.message}
              {...regTrade('transactionDate')}
            />
          </div>

          {/* Dynamic Trade Amount */}
          {watchTrade('quantity') && watchTrade('price') && Number(watchTrade('quantity')) > 0 && Number(watchTrade('price')) > 0 ? (
            <div className="p-3 rounded-lg bg-fin-panel border border-fin-border flex justify-between items-center text-xs">
              <span className="text-fin-muted">Estimated Trade Gross Value:</span>
              <span className="font-mono font-bold text-white text-sm">
                {formatCurrency(
                  Number(watchTrade('quantity')) * Number(watchTrade('price')) + (Number(watchTrade('fees')) || 0),
                  portfolio?.baseCurrency || 'INR'
                )}
              </span>
            </div>
          ) : null}

          <Input
            label="Notes (Optional)"
            placeholder="e.g. Monthly SIP or quarterly rebalancing trade"
            {...regTrade('notes')}
          />

          <div className="pt-4 flex items-center justify-end gap-3">
            <Button type="button" variant="outline" onClick={() => setIsAddTradeOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" isLoading={addTradeMutation.isPending}>
              Record Trade
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
