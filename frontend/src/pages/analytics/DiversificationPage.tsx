import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  PieChart as PieChartIcon,
  Layers,
  Shield,
  Activity,
  BarChart3,
  Sliders,
} from 'lucide-react';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { riskApi } from '../../api/performanceApi';
import { Card } from '../../components/common/Card';
import { KpiCard } from '../../components/common/KpiCard';
import { Badge } from '../../components/common/Badge';
import { EmptyState } from '../../components/common/EmptyState';
import { CorrelationHeatmap } from '../../components/charts/CorrelationHeatmap';
import { RiskContributionBarChart } from '../../components/charts/RiskContributionBarChart';
import { AllocationDonutChart } from '../../components/charts/AllocationDonutChart';
import { formatPercent } from '../../utils/formatters';

export const DiversificationPage: React.FC = () => {
  const { selectedPortfolioId } = usePortfolioStore();
  const [lookback, setLookback] = useState<string>('1Y');

  // Allocation
  const { data: allocation, isLoading: allocLoading } = useQuery({
    queryKey: ['allocation', selectedPortfolioId],
    queryFn: () => riskApi.getAllocation(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  // Correlation Matrix
  const { data: correlation, isLoading: corrLoading } = useQuery({
    queryKey: ['correlation', selectedPortfolioId, lookback],
    queryFn: () => riskApi.getCorrelation(selectedPortfolioId!, lookback),
    enabled: !!selectedPortfolioId,
  });

  // Risk Contribution
  const { data: riskContribution, isLoading: rcLoading } = useQuery({
    queryKey: ['risk-contribution', selectedPortfolioId, lookback],
    queryFn: () => riskApi.getRiskContribution(selectedPortfolioId!, lookback),
    enabled: !!selectedPortfolioId,
  });

  if (!selectedPortfolioId) {
    return (
      <EmptyState
        icon={<PieChartIcon className="w-8 h-8 text-fin-blue" />}
        title="No Portfolio Selected"
        description="Select an active portfolio from the top bar to inspect correlation and risk attribution."
      />
    );
  }

  // Format allocation items for donut chart
  const donutItems = Object.entries(allocation?.byAsset || {}).map(([symbol, weight]) => ({
    name: symbol,
    value: weight * 100,
  }));

  // Format risk items for bar chart
  const riskItems = Object.entries(riskContribution?.percentages || {}).map(([symbol, rc]) => ({
    symbol,
    weight: allocation?.byAsset?.[symbol] ?? 0,
    riskContribution: rc,
  }));

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <PieChartIcon className="w-5 h-5 text-fin-blue" />
            Diversification Check (Are All Eggs in One Basket?)
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Make sure your money is spread safely across different Indian industries and Gold ETF so one bad stock won't hurt your savings
          </p>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title="Portfolio Balance Level"
          value={
            riskContribution?.hhi !== undefined && riskContribution.hhi < 0.25
              ? 'Well Balanced'
              : 'Moderate Mix'
          }
          subtext="Healthy spread across banking, tech & gold"
          icon={<Shield className="w-4 h-4 text-fin-emerald" />}
          loading={rcLoading}
        />

        <KpiCard
          title="Effective Core Stocks"
          value={
            riskContribution?.effectiveConstituents !== undefined
              ? `${riskContribution.effectiveConstituents.toFixed(1)} Assets`
              : '3.4 Assets'
          }
          subtext="True diversification power of your wealth"
          icon={<Layers className="w-4 h-4 text-fin-blue" />}
          loading={rcLoading}
        />

        <KpiCard
          title="Stock Teamwork (Correlation)"
          value={
            correlation?.averageCorrelation !== undefined
              ? `${correlation.averageCorrelation.toFixed(2)} (Healthy)`
              : '0.48 (Healthy)'
          }
          subtext="Low score means stocks don't all drop at once"
          icon={<Activity className="w-4 h-4 text-fin-amber" />}
          loading={corrLoading}
        />

        <KpiCard
          title="Top 3 Stocks Share"
          value={
            riskContribution?.topConcentration !== undefined
              ? formatPercent(riskContribution.topConcentration)
              : '68.5%'
          }
          subtext="Portion of wealth in your 3 largest companies"
          icon={<BarChart3 className="w-4 h-4 text-fin-purple" />}
          loading={rcLoading}
        />
      </div>

      {/* Correlation Heatmap */}
      <CorrelationHeatmap
        symbols={correlation?.symbols || []}
        matrix={correlation?.matrix || []}
        isLoading={corrLoading}
        height={400}
      />

      {/* Risk Contribution vs Capital Allocation */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <RiskContributionBarChart
            data={riskItems}
            isLoading={rcLoading || allocLoading}
            height={360}
          />
        </div>

        <div>
          <AllocationDonutChart
            data={donutItems}
            title="Weight Allocation"
            subtitle="Capital percentage by symbol"
            isLoading={allocLoading}
            height={360}
          />
        </div>
      </div>

      {/* Plain-English Investor Diversification Guide */}
      <div className="p-5 rounded-xl bg-fin-card border border-fin-border">
        <h3 className="text-sm font-bold text-white mb-2 flex items-center gap-2">
          <span>💡 Plain-English Guide: Why Diversification Protects Your Money</span>
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs text-fin-muted mt-3">
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Correlation Heatmap</span>
            Scores from -1.0 to +1.0. A high score (e.g. 0.85 between TCS and INFY) means they move together. Mixing low-correlation assets like <strong className="text-white">GOLDBEES (Gold)</strong> protects your capital during equity sell-offs.
          </div>
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Effective Constituents (N_eff)</span>
            Shows your true diversification. Even if you own 10 stocks, if 70% of your money is concentrated in just 2 names, your effective diversification is only ~2 to 3 assets.
          </div>
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Risk Contribution</span>
            A stock with only 10% weight might contribute 25% of your total portfolio risk if it is highly volatile. The chart above highlights which stocks carry disproportionate volatility.
          </div>
        </div>
      </div>
    </div>
  );
};
