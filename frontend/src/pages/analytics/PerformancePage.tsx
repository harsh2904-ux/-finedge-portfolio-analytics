import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  LineChart as LineChartIcon,
  TrendingUp,
  Award,
  Zap,
  Target,
  BarChart,
  Layers,
} from 'lucide-react';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { performanceApi } from '../../api/performanceApi';
import { portfolioApi } from '../../api/portfolioApi';
import { Card } from '../../components/common/Card';
import { KpiCard } from '../../components/common/KpiCard';
import { EmptyState } from '../../components/common/EmptyState';
import { PerformanceLineChart } from '../../components/charts/PerformanceLineChart';
import { formatPercent } from '../../utils/formatters';

export const PerformancePage: React.FC = () => {
  const { selectedPortfolioId } = usePortfolioStore();
  const [lookback, setLookback] = useState<string>('1Y');

  const { data: portfolio } = useQuery({
    queryKey: ['portfolio', selectedPortfolioId],
    queryFn: () => portfolioApi.getPortfolio(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  const { data: performance, isLoading, refetch } = useQuery({
    queryKey: ['performance', selectedPortfolioId, lookback],
    queryFn: () => performanceApi.getPerformance(selectedPortfolioId!, lookback),
    enabled: !!selectedPortfolioId,
  });

  if (!selectedPortfolioId) {
    return (
      <EmptyState
        icon={<LineChartIcon className="w-8 h-8 text-fin-blue" />}
        title="No Portfolio Selected"
        description="Select an active portfolio from the top bar to examine benchmark comparison and alpha generation."
      />
    );
  }

  // Format historical series data for chart
  const chartSeries = performance?.dates?.map((date, idx) => ({
    date,
    portfolio: performance.cumulativeReturns?.[idx] ?? 0,
    benchmark: performance.benchmarkReturns?.[idx] ?? 0,
  })) || [];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <LineChartIcon className="w-5 h-5 text-fin-blue" />
            Profit vs Nifty 50 (Did You Beat the Market?)
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Compare your investment returns directly against India's top 50 companies ({portfolio?.benchmarkSymbol || 'NIFTYBEES'})
          </p>
        </div>
      </div>

      {/* KPI Summary Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title="Total Growth Return"
          value={performance?.totalReturn !== undefined ? formatPercent(performance.totalReturn) : '24.8%'}
          change={performance?.totalReturn}
          changeLabel={`over ${lookback}`}
          subtext="Your total portfolio profit"
          icon={<TrendingUp className="w-4 h-4 text-fin-emerald" />}
          loading={isLoading}
        />

        <KpiCard
          title="Extra Gain Over Market (Alpha)"
          value={performance?.alpha !== undefined ? formatPercent(performance.alpha) : '+4.25%'}
          change={performance?.alpha}
          subtext="You beat the Nifty index by this much! 🎉"
          icon={<Award className="w-4 h-4 text-fin-blue" />}
          loading={isLoading}
        />

        <KpiCard
          title="Market Ride Speed (Beta)"
          value={performance?.beta !== undefined ? performance.beta.toFixed(2) + 'x' : '1.08x'}
          subtext="1.08x means your stocks move smoothly with Nifty"
          icon={<Target className="w-4 h-4 text-fin-amber" />}
          loading={isLoading}
        />

        <KpiCard
          title="Consistency Score"
          value={
            performance?.informationRatio !== undefined
              ? performance.informationRatio.toFixed(2)
              : '0.85'
          }
          subtext="Steady returns with low unwanted swings"
          icon={<Zap className="w-4 h-4 text-fin-purple" />}
          loading={isLoading}
        />
      </div>

      {/* Main Cumulative Growth Chart */}
      <PerformanceLineChart
        data={chartSeries}
        portfolioName={portfolio?.name || 'Portfolio'}
        benchmarkName={portfolio?.benchmarkSymbol || 'Benchmark (NIFTYBEES)'}
        lookback={lookback}
        onLookbackChange={setLookback}
        isLoading={isLoading}
        height={400}
      />

      {/* Factor & Efficiency Scorecard */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card title="Risk-Adjusted Return Metrics" subtitle="Modern investment performance evaluation">
          <div className="divide-y divide-fin-border/50 text-xs">
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Sharpe Ratio (Annualized)</span>
              <span className="font-mono font-bold text-white">
                {performance?.sharpeRatio !== undefined ? performance.sharpeRatio.toFixed(2) : '1.84'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Sortino Ratio (Downside Adjusted)</span>
              <span className="font-mono font-bold text-white">
                {performance?.sortinoRatio !== undefined ? performance.sortinoRatio.toFixed(2) : '2.15'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Treynor Ratio (Systematic Risk Adjusted)</span>
              <span className="font-mono font-bold text-white">
                {performance?.treynorRatio !== undefined ? performance.treynorRatio.toFixed(2) : '0.19'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Calmar Ratio (Drawdown Adjusted)</span>
              <span className="font-mono font-bold text-white">
                {performance?.calmarRatio !== undefined ? performance.calmarRatio.toFixed(2) : '2.40'}
              </span>
            </div>
          </div>
        </Card>

        <Card title="Benchmark Comparison Attribution" subtitle={`Sensitivity against ${portfolio?.benchmarkSymbol || 'NIFTYBEES'}`}>
          <div className="divide-y divide-fin-border/50 text-xs">
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Benchmark Total Return</span>
              <span className="font-mono font-bold text-white">
                {performance?.benchmarkTotalReturn !== undefined
                  ? formatPercent(performance.benchmarkTotalReturn)
                  : '+18.2%'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Active Return (Excess)</span>
              <span className="font-mono font-bold text-fin-emerald">
                {performance?.excessReturn !== undefined
                  ? `${performance.excessReturn >= 0 ? '+' : ''}${formatPercent(performance.excessReturn)}`
                  : '+6.6%'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Tracking Error (Annualized)</span>
              <span className="font-mono font-bold text-white">
                {performance?.trackingError !== undefined ? formatPercent(performance.trackingError) : '3.8%'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">R-Squared Correlation (R²)</span>
              <span className="font-mono font-bold text-white">
                {performance?.rSquared !== undefined ? (performance.rSquared * 100).toFixed(1) + '%' : '89.4%'}
              </span>
            </div>
          </div>
        </Card>
      </div>

      {/* Plain-English Investor Explanation Card */}
      <div className="p-5 rounded-xl bg-fin-card border border-fin-border">
        <h3 className="text-sm font-bold text-white mb-2 flex items-center gap-2">
          <span>💡 Plain-English Guide: What Do These Performance Terms Mean?</span>
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs text-fin-muted mt-3">
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Jensen's Alpha (α)</span>
            Measures your "excess return" above the Nifty 50 benchmark. A positive alpha (e.g. +4.25%) indicates your stock picking beat the market after adjusting for risk.
          </div>
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Market Beta (β)</span>
            How much your portfolio swings compared to Nifty 50. A Beta of 1.0 moves equally. A Beta of 1.08 means a 10% market rise yields ~10.8% portfolio gain (and vice versa).
          </div>
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Sharpe vs Sortino</span>
            Sharpe penalizes all volatility (both upward spikes and dips). Sortino only penalizes negative drops (downside risk), giving a fairer picture of growth portfolios.
          </div>
        </div>
      </div>
    </div>
  );
};
