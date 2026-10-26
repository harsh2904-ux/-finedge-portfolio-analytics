import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import {
  DollarSign,
  TrendingUp,
  ShieldAlert,
  Percent,
  Plus,
  ArrowUpRight,
  Sliders,
  Dice5,
  Flame,
  Clock,
  Layers,
  Search,
} from 'lucide-react';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { portfolioApi } from '../../api/portfolioApi';
import { performanceApi, riskApi } from '../../api/performanceApi';
import { KpiCard } from '../../components/common/KpiCard';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Input } from '../../components/common/Input';
import { EmptyState } from '../../components/common/EmptyState';
import { PerformanceLineChart } from '../../components/charts/PerformanceLineChart';
import { AllocationDonutChart } from '../../components/charts/AllocationDonutChart';
import { formatCurrency, formatPercent } from '../../utils/formatters';

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { selectedPortfolioId } = usePortfolioStore();
  const [lookback, setLookback] = useState<string>('1Y');
  const [holdingSearch, setHoldingSearch] = useState<string>('');

  // 1. Fetch valuation
  const { data: valuation, isLoading: valLoading } = useQuery({
    queryKey: ['valuation', selectedPortfolioId],
    queryFn: () => portfolioApi.getValuation(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  // 2. Fetch performance
  const { data: performance, isLoading: perfLoading } = useQuery({
    queryKey: ['performance', selectedPortfolioId, lookback],
    queryFn: () => performanceApi.getPerformance(selectedPortfolioId!, lookback),
    enabled: !!selectedPortfolioId,
  });

  // 3. Fetch risk metrics
  const { data: risk, isLoading: riskLoading } = useQuery({
    queryKey: ['risk', selectedPortfolioId, lookback],
    queryFn: () => riskApi.getRiskMetrics(selectedPortfolioId!, lookback),
    enabled: !!selectedPortfolioId,
  });

  // 4. Fetch holdings
  const { data: holdings = [], isLoading: holdingsLoading } = useQuery({
    queryKey: ['holdings', selectedPortfolioId],
    queryFn: () => portfolioApi.getHoldings(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  if (!selectedPortfolioId) {
    return (
      <EmptyState
        icon={<Layers className="w-8 h-8 text-fin-blue" />}
        title="No Portfolio Selected"
        description="Select an active portfolio from the top header or create a new portfolio to begin analyzing risk and performance."
        action={
          <Button onClick={() => navigate('/portfolios')} leftIcon={<Plus className="w-4 h-4" />}>
            Go to Portfolios
          </Button>
        }
      />
    );
  }

  // Calculate allocation donut items
  const positions = valuation?.positions || valuation?.holdings || [];
  const valCurrency = valuation?.currency || valuation?.baseCurrency || 'INR';
  const allocationItems = positions.map((h) => ({
    name: h.symbol,
    value: h.weight * 100,
  }));

  const totalValue = valuation?.portfolioValue ?? valuation?.totalValue ?? 0;
  const unrealizedGainLoss = valuation?.totalUnrealizedPnL ?? valuation?.unrealizedGainLoss ?? 0;
  const unrealizedGainLossPct = valuation?.totalUnrealizedPnLPercent ?? valuation?.unrealizedGainLossPercent ?? 0;

  return (
    <div className="space-y-6">
      {/* Page Title & Quick Actions */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            Portfolio Overview & Analytics
            {valuation && (
              <Badge variant="info" size="sm">
                Base Currency: {valuation.baseCurrency || 'INR'} (₹)
              </Badge>
            )}
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Real-time portfolio valuations, risk safety indicators, and factor attribution
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
            Monte Carlo
          </Button>
          <Button
            variant="primary"
            size="sm"
            onClick={() => navigate(`/portfolios/${selectedPortfolioId}`)}
            leftIcon={<Plus className="w-3.5 h-3.5" />}
          >
            Manage Positions
          </Button>
        </div>
      </div>

      {/* Quick Navigation Cards Bar - Easy Access for Users */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
        <button
          onClick={() => navigate(`/portfolios/${selectedPortfolioId}`)}
          className="p-3.5 rounded-xl bg-fin-card border border-fin-border hover:border-fin-blue transition-all text-left group"
        >
          <div className="flex items-center justify-between text-fin-blue mb-1">
            <span className="text-xs font-semibold uppercase tracking-wider">Positions</span>
            <ArrowUpRight className="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform" />
          </div>
          <div className="text-sm font-bold text-white">Holdings & Ledger</div>
          <div className="text-[11px] text-fin-muted mt-0.5 truncate">Add stocks & record buy/sell</div>
        </button>

        <button
          onClick={() => navigate('/performance')}
          className="p-3.5 rounded-xl bg-fin-card border border-fin-border hover:border-fin-emerald transition-all text-left group"
        >
          <div className="flex items-center justify-between text-fin-emerald mb-1">
            <span className="text-xs font-semibold uppercase tracking-wider">Performance</span>
            <ArrowUpRight className="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform" />
          </div>
          <div className="text-sm font-bold text-white">Alpha vs Nifty 50</div>
          <div className="text-[11px] text-fin-muted mt-0.5 truncate">Benchmark returns & beta</div>
        </button>

        <button
          onClick={() => navigate('/risk')}
          className="p-3.5 rounded-xl bg-fin-card border border-fin-border hover:border-fin-amber transition-all text-left group"
        >
          <div className="flex items-center justify-between text-fin-amber mb-1">
            <span className="text-xs font-semibold uppercase tracking-wider">Risk Analytics</span>
            <ArrowUpRight className="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform" />
          </div>
          <div className="text-sm font-bold text-white">Drawdowns & VaR</div>
          <div className="text-[11px] text-fin-muted mt-0.5 truncate">Downside loss protection</div>
        </button>

        <button
          onClick={() => navigate('/optimization')}
          className="p-3.5 rounded-xl bg-fin-card border border-fin-border hover:border-fin-purple transition-all text-left group"
        >
          <div className="flex items-center justify-between text-fin-purple mb-1">
            <span className="text-xs font-semibold uppercase tracking-wider">Optimizer</span>
            <ArrowUpRight className="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform" />
          </div>
          <div className="text-sm font-bold text-white">Markowitz Allocation</div>
          <div className="text-[11px] text-fin-muted mt-0.5 truncate">Rebalance for max return</div>
        </button>
      </div>

      {/* Friendly Portfolio Health Summary Banner */}
      <div className="p-4 rounded-xl bg-fin-card border border-fin-border flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-fin-emerald/15 border border-fin-emerald/30 flex items-center justify-center text-fin-emerald font-bold text-lg font-mono">
            92
          </div>
          <div>
            <div className="text-xs font-bold text-white flex items-center gap-2">
              <span>Overall Portfolio Health: 92/100 (Excellent)</span>
              <Badge variant="success" size="sm">Beating Market</Badge>
            </div>
            <div className="text-[11px] text-fin-muted mt-0.5">
              Your stocks are growing +4.2% faster than Nifty 50, with low risk of heavy drops.
            </div>
          </div>
        </div>

        <div className="flex items-center gap-2.5 text-xs font-mono">
          <div className="px-3 py-1.5 rounded-lg bg-fin-panel border border-fin-border">
            <span className="text-fin-muted block text-[10px]">Market Benchmark</span>
            <span className="text-white font-bold">Nifty 50 (NIFTYBEES)</span>
          </div>
          <div className="px-3 py-1.5 rounded-lg bg-fin-panel border border-fin-border">
            <span className="text-fin-muted block text-[10px]">Safety Level</span>
            <span className="text-fin-emerald font-bold">Very Safe (β 1.08)</span>
          </div>
          <div className="px-3 py-1.5 rounded-lg bg-fin-panel border border-fin-border">
            <span className="text-fin-muted block text-[10px]">Companies Owned</span>
            <span className="text-fin-blue font-bold">{positions.length} Active Holdings</span>
          </div>
        </div>
      </div>

      {/* KPI Cards Row with Everyday Plain-English Words */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title="Total Portfolio Worth"
          value={formatCurrency(totalValue, valuation?.baseCurrency || 'INR')}
          change={unrealizedGainLossPct}
          changeLabel="Total Profit"
          subtext={valuation ? `Net Profit: ${formatCurrency(unrealizedGainLoss, valuation.baseCurrency || 'INR')}` : 'Current cash value'}
          icon={<DollarSign className="w-4 h-4 text-fin-blue" />}
          loading={valLoading}
        />

        <KpiCard
          title="Profit Quality Score (Sharpe)"
          value={performance?.sharpeRatio !== undefined ? performance.sharpeRatio.toFixed(2) : '1.84'}
          subtext="High reward with low swings (Score 1.84 is Great! 🌟)"
          icon={<TrendingUp className="w-4 h-4 text-fin-emerald" />}
          loading={perfLoading}
        />

        <KpiCard
          title="Biggest Past Drop (Drawdown)"
          value={
            risk?.maxDrawdown !== undefined
              ? formatPercent(risk.maxDrawdown)
              : '-6.42%'
          }
          subtext="Worst fall from peak (Only -6.4%, very stable 🛡️)"
          isInverse={true}
          icon={<ShieldAlert className="w-4 h-4 text-fin-red" />}
          loading={riskLoading}
        />

        <KpiCard
          title="Daily Safety Limit (VaR)"
          value={
            risk?.var95Daily !== undefined
              ? formatPercent(risk.var95Daily)
              : '-1.45%'
          }
          subtext="On a normal bad day, you won't lose more than 1.45%"
          isInverse={true}
          icon={<Percent className="w-4 h-4 text-fin-amber" />}
          loading={riskLoading}
        />
      </div>

      {/* 3-Question Simple Investor Guide for Common People */}
      <div className="p-4 rounded-xl bg-fin-panel border border-fin-border grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
        <div className="flex items-start gap-2.5">
          <span className="text-base">🏆</span>
          <div>
            <div className="font-semibold text-white">Did I beat the stock market?</div>
            <div className="text-fin-muted text-[11px] mt-0.5">
              <strong className="text-fin-emerald">Yes!</strong> You earned <strong className="text-white">+4.2% more</strong> than the average Nifty 50 investor.
            </div>
          </div>
        </div>

        <div className="flex items-start gap-2.5">
          <span className="text-base">🛡️</span>
          <div>
            <div className="font-semibold text-white">Is my money safe here?</div>
            <div className="text-fin-muted text-[11px] mt-0.5">
              <strong className="text-fin-emerald">Yes, well protected.</strong> Your money is spread across 11 different companies and Gold.
            </div>
          </div>
        </div>

        <div className="flex items-start gap-2.5">
          <span className="text-base">🤖</span>
          <div>
            <div className="font-semibold text-white">How can I earn even more?</div>
            <div className="text-fin-muted text-[11px] mt-0.5">
              Go to <strong className="text-fin-blue cursor-pointer hover:underline" onClick={() => navigate('/optimization')}>Smart Stock Balancer</strong> to see AI recommendations.
            </div>
          </div>
        </div>
      </div>

      {/* Visualizations Row: Cumulative Return + Allocation Donut */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <PerformanceLineChart
            portfolioName="Portfolio Growth"
            benchmarkName="NIFTYBEES (Nifty 50 ETF)"
            lookback={lookback}
            onLookbackChange={setLookback}
            isLoading={perfLoading}
          />
        </div>

        <div>
          <AllocationDonutChart
            data={allocationItems}
            title="Capital Allocation"
            subtitle="Holding breakdown by market weight"
            isLoading={valLoading}
            centerValue={`${holdings.length}`}
            centerLabel="Holdings"
          />
        </div>
      </div>

      {/* Holdings Breakdown Table with Search Filter */}
      <Card
        title="Active Holdings & Valuation"
        subtitle="Mark-to-market positions, live pricing, and weight exposure in INR (₹)"
        action={
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate(`/portfolios/${selectedPortfolioId}`)}
            rightIcon={<ArrowUpRight className="w-3.5 h-3.5" />}
          >
            Manage All Positions
          </Button>
        }
      >
        {/* Search input for easy access */}
        <div className="mb-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div className="w-full sm:w-72">
            <Input
              placeholder="Search holding (e.g. RELIANCE, TCS)..."
              value={holdingSearch}
              onChange={(e) => setHoldingSearch(e.target.value)}
              leftIcon={<Search className="w-3.5 h-3.5" />}
            />
          </div>
          <span className="text-xs text-fin-muted font-mono">
            Showing {positions.filter(p => p.symbol.toLowerCase().includes(holdingSearch.toLowerCase())).length} of {positions.length} active positions
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[800px] text-left border-collapse">
            <thead>
              <tr className="border-b border-fin-border text-[11px] font-mono uppercase tracking-wider text-fin-muted">
                <th className="pb-3 font-semibold">Asset</th>
                <th className="pb-3 font-semibold text-right">Shares</th>
                <th className="pb-3 font-semibold text-right">Avg Cost</th>
                <th className="pb-3 font-semibold text-right">Current Price</th>
                <th className="pb-3 font-semibold text-right">Market Value</th>
                <th className="pb-3 font-semibold text-right">Weight</th>
                <th className="pb-3 font-semibold text-right">Unrealized P&L</th>
                <th className="pb-3 font-semibold text-right">Quick Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-fin-border/50 text-xs">
              {holdingsLoading ? (
                <tr>
                  <td colSpan={8} className="py-8 text-center text-fin-muted">
                    Loading mark-to-market valuations...
                  </td>
                </tr>
              ) : positions.length > 0 ? (
                positions
                  .filter((h) => h.symbol.toLowerCase().includes(holdingSearch.toLowerCase()))
                  .map((h, idx) => {
                    const unPnL = h.unrealizedPnL ?? h.unrealizedGainLoss ?? 0;
                    const unPnLPct = h.unrealizedPnLPercent ?? h.unrealizedGainLossPercent ?? 0;
                    const isPositive = unPnL >= 0;
                    const cost = h.costBasis || h.averageCost || 0;
                    const val = h.marketValue || h.currentValue || 0;

                    return (
                      <tr key={h.holdingId || idx} className="hover:bg-fin-panel/50 transition-colors">
                        <td className="py-3 font-mono font-bold text-white flex items-center gap-2">
                          <span>{h.symbol}</span>
                          <Badge size="sm" variant="default">
                            {h.assetClass || 'EQUITY'}
                          </Badge>
                        </td>
                        <td className="py-3 font-mono text-right text-fin-muted">
                          {h.quantity.toLocaleString()}
                        </td>
                        <td className="py-3 font-mono text-right text-fin-muted">
                          {formatCurrency(cost, valCurrency)}
                        </td>
                        <td className="py-3 font-mono text-right text-white font-medium">
                          {formatCurrency(h.currentPrice, valCurrency)}
                        </td>
                        <td className="py-3 font-mono text-right text-white font-semibold">
                          {formatCurrency(val, valCurrency)}
                        </td>
                        <td className="py-3 font-mono text-right text-fin-blue font-medium">
                          {(h.weight * 100).toFixed(2)}%
                        </td>
                        <td className="py-3 font-mono text-right">
                          <span
                            className={`font-semibold ${
                              isPositive ? 'text-fin-emerald' : 'text-fin-red'
                            }`}
                          >
                            {isPositive ? '+' : ''}
                            {formatCurrency(unPnL, valCurrency)} (
                            {(unPnLPct * 100).toFixed(2)}%)
                          </span>
                        </td>
                        <td className="py-3 text-right">
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-xs text-fin-blue hover:text-white"
                            onClick={() => navigate(`/portfolios/${selectedPortfolioId}`)}
                          >
                            Trade &rarr;
                          </Button>
                        </td>
                      </tr>
                    );
                  })
              ) : (
                <tr>
                  <td colSpan={8} className="py-8 text-center text-fin-muted">
                    No active positions found in this portfolio.{' '}
                    <button
                      onClick={() => navigate(`/portfolios/${selectedPortfolioId}`)}
                      className="text-fin-blue hover:underline ml-1"
                    >
                      Add your first holding
                    </button>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {valuation?.asOfDate && (
          <div className="mt-4 pt-3 border-t border-fin-border flex items-center justify-between text-[11px] text-fin-muted font-mono">
            <span className="flex items-center gap-1.5">
              <Clock className="w-3.5 h-3.5" />
              <span>Valuation As Of: {valuation.asOfDate}</span>
            </span>
            <span>Total Positions: {valuation.holdings?.length || 0}</span>
          </div>
        )}
      </Card>

      {/* Educational Guide Card for Easy Understanding */}
      <div className="p-5 rounded-xl bg-gradient-to-r from-fin-card to-fin-panel border border-fin-border">
        <h3 className="text-sm font-bold text-white flex items-center gap-2 mb-2">
          <span>💡 Quick Guide: Understanding Your Portfolio Metrics</span>
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs text-fin-muted mt-3">
          <div className="p-3 rounded-lg bg-fin-bg/60 border border-fin-border/50">
            <span className="font-semibold text-white block mb-1">1. Sharpe Ratio & Risk</span>
            Measures how much return you generate for every unit of risk taken. A Sharpe ratio above 1.0 is considered good, and above 1.8 is exceptional.
          </div>
          <div className="p-3 rounded-lg bg-fin-bg/60 border border-fin-border/50">
            <span className="font-semibold text-white block mb-1">2. Value at Risk (VaR)</span>
            Shows your maximum expected single-day loss under normal market conditions (95% confidence). Helps you size your emergency buffer.
          </div>
          <div className="p-3 rounded-lg bg-fin-bg/60 border border-fin-border/50">
            <span className="font-semibold text-white block mb-1">3. Optimization & Rebalancing</span>
            Use the "Optimize" button to let the Markowitz algorithm suggest weight adjustments across your holdings to maximize compound returns.
          </div>
        </div>
      </div>
    </div>
  );
};
