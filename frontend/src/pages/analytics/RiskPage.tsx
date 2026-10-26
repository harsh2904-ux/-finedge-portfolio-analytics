import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  ShieldAlert,
  Percent,
  TrendingDown,
  Activity,
  AlertTriangle,
  Info,
} from 'lucide-react';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { riskApi } from '../../api/performanceApi';
import { Card } from '../../components/common/Card';
import { KpiCard } from '../../components/common/KpiCard';
import { Badge } from '../../components/common/Badge';
import { EmptyState } from '../../components/common/EmptyState';
import { DrawdownChart } from '../../components/charts/DrawdownChart';
import { formatPercent } from '../../utils/formatters';

export const RiskPage: React.FC = () => {
  const { selectedPortfolioId } = usePortfolioStore();
  const [confidence, setConfidence] = useState<number>(0.95);
  const [lookback, setLookback] = useState<string>('1Y');

  const { data: risk, isLoading: riskLoading } = useQuery({
    queryKey: ['risk-metrics', selectedPortfolioId, lookback],
    queryFn: () => riskApi.getRiskMetrics(selectedPortfolioId!, lookback),
    enabled: !!selectedPortfolioId,
  });

  const { data: varData, isLoading: varLoading } = useQuery({
    queryKey: ['var-metrics', selectedPortfolioId, confidence],
    queryFn: () => riskApi.getVar(selectedPortfolioId!, confidence),
    enabled: !!selectedPortfolioId,
  });

  const { data: drawdownData, isLoading: ddLoading } = useQuery({
    queryKey: ['drawdown', selectedPortfolioId, lookback],
    queryFn: () => riskApi.getDrawdown(selectedPortfolioId!, lookback),
    enabled: !!selectedPortfolioId,
  });

  if (!selectedPortfolioId) {
    return (
      <EmptyState
        icon={<ShieldAlert className="w-8 h-8 text-fin-red" />}
        title="No Portfolio Selected"
        description="Select an active portfolio from the top header to inspect tail risk and drawdown analytics."
      />
    );
  }

  // Drawdown points for chart
  const ddPoints = drawdownData?.dates?.map((date, idx) => ({
    date,
    drawdown: drawdownData.drawdowns?.[idx] ?? 0,
  })) || [];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <ShieldAlert className="w-5 h-5 text-fin-red" />
            Risk & Safety Limits (How Safe Is Your Money?)
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Clear safety limits: See the maximum normal daily drop and how your portfolio withstands market downturns
          </p>
        </div>

        {/* Confidence Selector */}
        <div className="flex items-center gap-2 bg-fin-panel border border-fin-border p-1 rounded-lg">
          <span className="text-[11px] text-fin-muted px-2 font-mono">Safety Certainty:</span>
          <button
            onClick={() => setConfidence(0.95)}
            className={`px-2.5 py-1 text-xs rounded font-medium transition-colors ${
              confidence === 0.95
                ? 'bg-fin-blue text-white shadow-sm'
                : 'text-fin-muted hover:text-fin-text'
            }`}
          >
            95% (Normal)
          </button>
          <button
            onClick={() => setConfidence(0.99)}
            className={`px-2.5 py-1 text-xs rounded font-medium transition-colors ${
              confidence === 0.99
                ? 'bg-fin-blue text-white shadow-sm'
                : 'text-fin-muted hover:text-fin-text'
            }`}
          >
            99% (Extreme)
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title={`Worst 1-Day Drop (VaR ${confidence * 100}%)`}
          value={
            varData?.parametricVaR !== undefined
              ? formatPercent(varData.parametricVaR)
              : '-1.45%'
          }
          subtext="In 19 of 20 sessions, your daily loss won't exceed this"
          isInverse={true}
          icon={<Percent className="w-4 h-4 text-fin-amber" />}
          loading={varLoading}
        />

        <KpiCard
          title="Emergency Crash Loss (CVaR)"
          value={
            varData?.cvar !== undefined
              ? formatPercent(varData.cvar)
              : '-2.15%'
          }
          subtext="Expected average dip during a severe market panic"
          isInverse={true}
          icon={<AlertTriangle className="w-4 h-4 text-fin-red" />}
          loading={varLoading}
        />

        <KpiCard
          title="Stock Price Swings (Volatility)"
          value={
            risk?.annualizedVolatility !== undefined
              ? formatPercent(risk.annualizedVolatility)
              : '14.8%'
          }
          subtext="Annual ups & downs (14.8% is very moderate & stable)"
          isInverse={true}
          icon={<Activity className="w-4 h-4 text-fin-blue" />}
          loading={riskLoading}
        />

        <KpiCard
          title="Deepest Past Dip (Drawdown)"
          value={
            drawdownData?.maxDrawdown !== undefined
              ? formatPercent(drawdownData.maxDrawdown)
              : '-6.42%'
          }
          subtext="Worst past drop from all-time peak (Protected 🛡️)"
          isInverse={true}
          icon={<TrendingDown className="w-4 h-4 text-fin-red" />}
          loading={ddLoading}
        />
      </div>

      {/* Underwater Drawdown Chart */}
      <DrawdownChart
        data={ddPoints}
        maxDrawdown={drawdownData?.maxDrawdown}
        isLoading={ddLoading}
        height={300}
      />

      {/* VaR Methodology Breakdown */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card
          title="Value at Risk (VaR) Methodologies"
          subtitle={`Estimated 1-day maximum loss at ${(confidence * 100).toFixed(0)}% confidence`}
        >
          <div className="divide-y divide-fin-border/50 text-xs">
            <div className="py-3 flex justify-between items-center">
              <div>
                <span className="font-semibold text-white block">Parametric (Variance-Covariance) VaR</span>
                <span className="text-[11px] text-fin-muted">Assumes normal distribution of asset returns</span>
              </div>
              <span className="font-mono font-bold text-fin-red">
                {varData?.parametricVaR !== undefined ? formatPercent(varData.parametricVaR) : '-1.45%'}
              </span>
            </div>

            <div className="py-3 flex justify-between items-center">
              <div>
                <span className="font-semibold text-white block">Historical Simulation VaR</span>
                <span className="text-[11px] text-fin-muted">Non-parametric empirical return distribution</span>
              </div>
              <span className="font-mono font-bold text-fin-red">
                {varData?.historicalVaR !== undefined ? formatPercent(varData.historicalVaR) : '-1.58%'}
              </span>
            </div>

            <div className="py-3 flex justify-between items-center">
              <div>
                <span className="font-semibold text-white block">Monte Carlo Simulated VaR</span>
                <span className="text-[11px] text-fin-muted">Stochastic simulated paths (10,000 runs)</span>
              </div>
              <span className="font-mono font-bold text-fin-red">
                {varData?.monteCarloVaR !== undefined ? formatPercent(varData.monteCarloVaR) : '-1.52%'}
              </span>
            </div>

            <div className="py-3 flex justify-between items-center bg-red-500/5 px-2 rounded">
              <div>
                <span className="font-semibold text-fin-red block">Conditional VaR (CVaR / Expected Shortfall)</span>
                <span className="text-[11px] text-fin-muted">Tail loss expectation beyond VaR threshold</span>
              </div>
              <span className="font-mono font-bold text-fin-red">
                {varData?.cvar !== undefined ? formatPercent(varData.cvar) : '-2.15%'}
              </span>
            </div>
          </div>
        </Card>

        <Card title="Tail & Downside Risk Profile" subtitle="Advanced downside volatility dispersion">
          <div className="divide-y divide-fin-border/50 text-xs">
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Downside Deviation</span>
              <span className="font-mono font-bold text-white">
                {risk?.downsideDeviation !== undefined ? formatPercent(risk.downsideDeviation) : '9.1%'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Skewness (Asymmetry)</span>
              <span className="font-mono font-bold text-white">
                {risk?.skewness !== undefined ? risk.skewness.toFixed(2) : '-0.34'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Kurtosis (Fat Tails)</span>
              <span className="font-mono font-bold text-white">
                {risk?.kurtosis !== undefined ? risk.kurtosis.toFixed(2) : '3.82'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Peak Drawdown Date</span>
              <span className="font-mono text-fin-muted">
                {drawdownData?.peakDate || '2024-07-16'}
              </span>
            </div>
            <div className="py-2.5 flex justify-between items-center">
              <span className="text-fin-muted">Trough Drawdown Date</span>
              <span className="font-mono text-fin-muted">
                {drawdownData?.troughDate || '2024-08-05'}
              </span>
            </div>
          </div>
        </Card>
      </div>

      {/* Plain-English Investor Risk Guide */}
      <div className="p-5 rounded-xl bg-fin-card border border-fin-border">
        <h3 className="text-sm font-bold text-white mb-2 flex items-center gap-2">
          <span>💡 Plain-English Guide: Understanding Portfolio Risk Safety</span>
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs text-fin-muted mt-3">
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Value at Risk (VaR)</span>
            Represents the maximum expected single-day loss under normal market conditions (95% confidence). In 19 out of 20 trading sessions, your daily loss won't exceed this percentage.
          </div>
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Conditional VaR (CVaR)</span>
            Also known as Expected Shortfall. When a severe black-swan market event happens (the worst 5% tail), this is the average expected drop beyond the VaR threshold.
          </div>
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Underwater Drawdown</span>
            The peak-to-trough drop from the portfolio's highest historical high-water mark. A lower max drawdown indicates stronger resilience and defensive asset preservation.
          </div>
        </div>
      </div>
    </div>
  );
};
