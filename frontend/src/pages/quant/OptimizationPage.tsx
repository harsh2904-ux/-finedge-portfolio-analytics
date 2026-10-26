import React, { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import {
  Sliders,
  Play,
  TrendingUp,
  Shield,
  ArrowRight,
  CheckCircle2,
  AlertCircle,
  Clock,
  Sparkles,
} from 'lucide-react';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { optimizationApi } from '../../api/optimizationApi';
import { portfolioApi } from '../../api/portfolioApi';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Select } from '../../components/common/Select';
import { Input } from '../../components/common/Input';
import { EmptyState } from '../../components/common/EmptyState';
import { EfficientFrontierChart } from '../../components/charts/EfficientFrontierChart';
import { formatPercent, formatCurrency } from '../../utils/formatters';
import type { OptimizationObjective } from '../../types/optimization';

export const OptimizationPage: React.FC = () => {
  const { selectedPortfolioId } = usePortfolioStore();

  const [objective, setObjective] = useState<OptimizationObjective>('MAX_SHARPE');
  const [minWeight, setMinWeight] = useState<number>(0.0);
  const [maxWeight, setMaxWeight] = useState<number>(0.4);
  const [targetReturn, setTargetReturn] = useState<number>(0.15);

  // Fetch valuation to know current allocation and assets
  const { data: valuation } = useQuery({
    queryKey: ['valuation', selectedPortfolioId],
    queryFn: () => portfolioApi.getValuation(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  // Efficient Frontier Query
  const { data: frontierData, isLoading: frontierLoading, refetch: refetchFrontier } = useQuery({
    queryKey: ['efficient-frontier', selectedPortfolioId],
    queryFn: () =>
      optimizationApi.getEfficientFrontier(selectedPortfolioId!, {
        points: 25,
        minWeight,
        maxWeight,
      }),
    enabled: !!selectedPortfolioId,
  });

  // Optimize Mutation
  const optimizeMutation = useMutation({
    mutationFn: () =>
      optimizationApi.optimize(selectedPortfolioId!, {
        objective,
        minWeight,
        maxWeight,
        targetReturn: objective === 'TARGET_RETURN' ? targetReturn : undefined,
      }),
  });

  if (!selectedPortfolioId) {
    return (
      <EmptyState
        icon={<Sliders className="w-8 h-8 text-fin-blue" />}
        title="No Portfolio Selected"
        description="Select an active portfolio from the top bar to run Markowitz portfolio optimization."
      />
    );
  }

  const handleRunOptimization = () => {
    optimizeMutation.mutate();
  };

  const optResult = optimizeMutation.data?.result;
  const efResult = frontierData?.result;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <Sliders className="w-5 h-5 text-fin-blue" />
            Smart Stock Balancer (Get Best Return With Least Risk)
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Tell the AI your goal, and it will calculate the exact shares of Reliance, TCS, HDFC Bank, and Gold to buy or sell
          </p>
        </div>

        <Button
          variant="primary"
          size="md"
          onClick={handleRunOptimization}
          isLoading={optimizeMutation.isPending}
          leftIcon={<Play className="w-4 h-4 fill-current" />}
        >
          Calculate Smart Mix (1-Click)
        </Button>
      </div>

      {/* 3 One-Click Strategy Preset Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <button
          type="button"
          onClick={() => {
            setObjective('MAX_SHARPE');
            setMinWeight(0.0);
            setMaxWeight(0.4);
          }}
          className={`p-4 rounded-xl border text-left transition-all ${
            objective === 'MAX_SHARPE'
              ? 'bg-fin-card border-fin-blue ring-1 ring-fin-blue/60 shadow-lg'
              : 'bg-fin-card/60 border-fin-border hover:border-fin-border/80'
          }`}
        >
          <div className="flex items-center justify-between mb-2">
            <Badge variant="success" size="sm">Most Popular</Badge>
            <TrendingUp className="w-4 h-4 text-fin-emerald" />
          </div>
          <div className="text-sm font-bold text-white">Option 1: Max Profit per Risk (Recommended)</div>
          <div className="text-xs text-fin-muted mt-1 leading-relaxed">
            Delivers the highest profit with minimal unnecessary price swings. Best for everyday investors building long-term wealth.
          </div>
        </button>

        <button
          type="button"
          onClick={() => {
            setObjective('MIN_VOLATILITY');
            setMinWeight(0.0);
            setMaxWeight(0.35);
          }}
          className={`p-4 rounded-xl border text-left transition-all ${
            objective === 'MIN_VOLATILITY'
              ? 'bg-fin-card border-fin-blue ring-1 ring-fin-blue/60 shadow-lg'
              : 'bg-fin-card/60 border-fin-border hover:border-fin-border/80'
          }`}
        >
          <div className="flex items-center justify-between mb-2">
            <Badge variant="info" size="sm">Capital Safety</Badge>
            <Shield className="w-4 h-4 text-fin-blue" />
          </div>
          <div className="text-sm font-bold text-white">Option 2: Maximum Capital Safety</div>
          <div className="text-xs text-fin-muted mt-1 leading-relaxed">
            Protects your hard-earned money from big market crashes by favoring low-volatility stocks and Gold ETF.
          </div>
        </button>

        <button
          type="button"
          onClick={() => {
            setObjective('TARGET_RETURN');
            setMinWeight(0.0);
            setMaxWeight(0.5);
            setTargetReturn(0.18);
          }}
          className={`p-4 rounded-xl border text-left transition-all ${
            objective === 'TARGET_RETURN'
              ? 'bg-fin-card border-fin-blue ring-1 ring-fin-blue/60 shadow-lg'
              : 'bg-fin-card/60 border-fin-border hover:border-fin-border/80'
          }`}
        >
          <div className="flex items-center justify-between mb-2">
            <Badge variant="warning" size="sm">High Growth</Badge>
            <Sparkles className="w-4 h-4 text-fin-amber" />
          </div>
          <div className="text-sm font-bold text-white">Option 3: Fast Growth Target (18%+)</div>
          <div className="text-xs text-fin-muted mt-1 leading-relaxed">
            Targets higher annual gains (18%+) by smartly picking high-momentum growth companies.
          </div>
        </button>
      </div>

      {/* Configuration Controls Bar */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 p-4 rounded-xl bg-fin-card border border-fin-border">
        <Select
          label="Optimization Objective"
          value={objective}
          onChange={(e) => setObjective(e.target.value as OptimizationObjective)}
          options={[
            { value: 'MAX_SHARPE', label: 'Maximize Sharpe Ratio (Tangency)' },
            { value: 'MIN_VOLATILITY', label: 'Minimize Volatility (Min Risk)' },
            { value: 'TARGET_RETURN', label: 'Target Return Constraint' },
          ]}
        />

        <Input
          label="Min Asset Weight (Lower Bound)"
          type="number"
          step="0.05"
          min="0"
          max="0.5"
          value={minWeight}
          onChange={(e) => setMinWeight(Number(e.target.value))}
          helperText="e.g. 0.0 for 0% minimum"
        />

        <Input
          label="Max Asset Weight (Position Cap)"
          type="number"
          step="0.05"
          min="0.1"
          max="1.0"
          value={maxWeight}
          onChange={(e) => setMaxWeight(Number(e.target.value))}
          helperText="e.g. 0.40 for 40% position limit"
        />

        {objective === 'TARGET_RETURN' ? (
          <Input
            label="Target Annual Return"
            type="number"
            step="0.01"
            value={targetReturn}
            onChange={(e) => setTargetReturn(Number(e.target.value))}
            helperText="e.g. 0.18 for 18% annual return"
          />
        ) : (
          <div className="flex flex-col justify-end pb-1.5">
            <span className="text-xs text-fin-muted mb-1">Portfolio Constraint:</span>
            <div className="text-xs font-mono text-white">Long-only Equities (Σw = 100%)</div>
          </div>
        )}
      </div>

      {/* Efficient Frontier Interactive Chart */}
      <EfficientFrontierChart
        frontierPoints={efResult?.frontierPoints || []}
        currentPoint={
          optResult?.current
            ? {
                volatility: optResult.current.volatility,
                returnVal: optResult.current.expectedReturn,
                sharpe: optResult.current.sharpeRatio,
              }
            : efResult?.currentPoint
        }
        maxSharpePoint={
          optResult?.optimized
            ? {
                volatility: optResult.optimized.volatility,
                returnVal: optResult.optimized.expectedReturn,
                sharpe: optResult.optimized.sharpeRatio,
              }
            : efResult?.maxSharpePoint
        }
        minVolPoint={efResult?.minVolPoint}
        individualAssets={efResult?.assetPoints}
        isLoading={frontierLoading}
        height={400}
      />

      {/* Optimization Results / Comparison Grid */}
      {optResult?.current && optResult?.optimized ? (() => {
        const current = optResult.current;
        const optimized = optResult.optimized;
        return (
          <div className="space-y-6">
            {/* Friendly Executive Summary Banner */}
            <div className="p-4 rounded-xl bg-gradient-to-r from-fin-blue/15 to-fin-emerald/10 border border-fin-blue/30 flex flex-wrap items-center justify-between gap-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-fin-blue/20 flex items-center justify-center text-fin-blue">
                  <Sparkles className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-sm font-bold text-white flex items-center gap-2">
                    Optimization Results: Expected Annual Return of {formatPercent(optimized.expectedReturn)}
                    <Badge variant="success" size="sm">
                      +{formatPercent(optimized.expectedReturn - current.expectedReturn)} Boost
                    </Badge>
                  </div>
                  <div className="text-xs text-fin-muted mt-0.5">
                    Sharpe ratio improves from {current.sharpeRatio.toFixed(2)} to {optimized.sharpeRatio.toFixed(2)}. Rebalance your Demat positions according to the checklist below.
                  </div>
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Current vs Optimized Metric Comparison */}
            <Card title="Risk-Return Impact Scorecard" subtitle="Expected improvement after rebalancing">
              <div className="space-y-4 text-xs">
                <div className="p-3 rounded-lg bg-fin-panel border border-fin-border flex items-center justify-between">
                  <div>
                    <div className="text-fin-muted">Sharpe Ratio</div>
                    <div className="text-base font-bold font-mono text-white">
                      {current.sharpeRatio.toFixed(2)} &rarr;{' '}
                      <span className="text-fin-emerald font-bold">
                        {optimized.sharpeRatio.toFixed(2)}
                      </span>
                    </div>
                  </div>
                  <Badge variant="success" size="sm">
                    +{(optimized.sharpeRatio - current.sharpeRatio).toFixed(2)}
                  </Badge>
                </div>

                <div className="p-3 rounded-lg bg-fin-panel border border-fin-border flex items-center justify-between">
                  <div>
                    <div className="text-fin-muted">Expected Annual Return</div>
                    <div className="text-base font-bold font-mono text-white">
                      {formatPercent(current.expectedReturn)} &rarr;{' '}
                      <span className="text-fin-emerald">
                        {formatPercent(optimized.expectedReturn)}
                      </span>
                    </div>
                  </div>
                  <Badge variant="info" size="sm">
                    +{formatPercent(optimized.expectedReturn - current.expectedReturn)}
                  </Badge>
                </div>

                <div className="p-3 rounded-lg bg-fin-panel border border-fin-border flex items-center justify-between">
                  <div>
                    <div className="text-fin-muted">Expected Volatility (Risk)</div>
                    <div className="text-base font-bold font-mono text-white">
                      {formatPercent(current.volatility)} &rarr;{' '}
                      <span className="text-white">
                        {formatPercent(optimized.volatility)}
                      </span>
                    </div>
                  </div>
                  <Badge
                    variant={
                      optimized.volatility <= current.volatility
                        ? 'success'
                        : 'warning'
                    }
                    size="sm"
                  >
                    {formatPercent(optimized.volatility - current.volatility)}
                  </Badge>
                </div>
              </div>
            </Card>

            {/* Allocation Rebalancing Trade Recommendations */}
            <div className="lg:col-span-2">
              <Card
                title="Recommended Rebalancing Actions"
                subtitle="Calculated stock weight adjustments to reach the optimal frontier"
              >
                <div className="overflow-x-auto">
                  <table className="w-full min-w-[680px] text-left border-collapse">
                    <thead>
                      <tr className="border-b border-fin-border text-[11px] font-mono uppercase tracking-wider text-fin-muted">
                        <th className="pb-3 font-semibold">Asset</th>
                        <th className="pb-3 font-semibold text-right">Current Weight</th>
                        <th className="pb-3 font-semibold text-right">Optimal Weight</th>
                        <th className="pb-3 font-semibold text-right">Target Action</th>
                        <th className="pb-3 font-semibold text-right">Est. Value (₹)</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-fin-border/50 text-xs">
                      {Object.keys(optimized.weights).map((sym) => {
                        const curr = current.weights[sym] ?? 0;
                        const opt = optimized.weights[sym] ?? 0;
                        const diff = opt - curr;
                        const isBuy = diff > 0.001;
                        const isSell = diff < -0.001;
                        const totalVal = valuation?.totalValue || valuation?.portfolioValue || 0;
                        const diffAmount = Math.abs(diff * totalVal);

                        return (
                          <tr key={sym} className="hover:bg-fin-panel/40">
                            <td className="py-2.5 font-mono font-bold text-white">{sym}</td>
                            <td className="py-2.5 font-mono text-right text-fin-muted">
                              {(curr * 100).toFixed(1)}%
                            </td>
                            <td className="py-2.5 font-mono text-right text-fin-blue font-semibold">
                              {(opt * 100).toFixed(1)}%
                            </td>
                            <td className="py-2.5 font-mono text-right">
                              {isBuy && (
                                <Badge variant="success" size="sm">
                                  BUY +{(diff * 100).toFixed(1)}%
                                </Badge>
                              )}
                              {isSell && (
                                <Badge variant="danger" size="sm">
                                  SELL {(diff * 100).toFixed(1)}%
                                </Badge>
                              )}
                              {!isBuy && !isSell && (
                                <Badge variant="default" size="sm">
                                  HOLD (0.0%)
                                </Badge>
                              )}
                            </td>
                            <td className="py-2.5 font-mono text-right text-fin-text font-medium">
                              {isBuy ? `+${formatCurrency(diffAmount, 'INR')}` : isSell ? `-${formatCurrency(diffAmount, 'INR')}` : '₹0'}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>

                <div className="mt-4 pt-3 border-t border-fin-border text-[11px] text-fin-muted leading-relaxed">
                  💡 <strong>How to rebalance:</strong> First liquidate / sell positions marked <span className="text-fin-red font-semibold">SELL</span> to free up liquidity in your Demat account, then deploy into positions marked <span className="text-fin-emerald font-semibold">BUY</span>.
                </div>
              </Card>
            </div>
          </div>
        </div>
      ); })() : (
        <div className="p-5 rounded-xl bg-fin-card border border-fin-border flex items-center justify-between text-xs text-fin-muted">
          <div className="flex items-center gap-3">
            <Sparkles className="w-5 h-5 text-fin-blue" />
            <span>
              Select your preferred strategy preset above and click <strong className="text-white">"Compute Optimal Allocation"</strong> to calculate your personalized optimal weights.
            </span>
          </div>
        </div>
      )}
    </div>
  );
};
