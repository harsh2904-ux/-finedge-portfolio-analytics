import React, { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import {
  Flame,
  Play,
  AlertTriangle,
  TrendingDown,
  Layers,
  ShieldAlert,
  Sliders,
  DollarSign,
} from 'lucide-react';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { scenarioApi } from '../../api/optimizationApi';
import { portfolioApi } from '../../api/portfolioApi';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Input } from '../../components/common/Input';
import { EmptyState } from '../../components/common/EmptyState';
import { formatCurrency, formatPercent } from '../../utils/formatters';
import type { StressTestResult } from '../../types/scenario';

export const ScenariosPage: React.FC = () => {
  const { selectedPortfolioId } = usePortfolioStore();

  const [customMarketDrop, setCustomMarketDrop] = useState<number>(-15);
  const [customRateShockBps, setCustomRateShockBps] = useState<number>(100);
  const [customVolSpike, setCustomVolSpike] = useState<number>(50);

  // Fetch valuation for dollar losses
  const { data: valuation } = useQuery({
    queryKey: ['valuation', selectedPortfolioId],
    queryFn: () => portfolioApi.getValuation(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  // Fetch predefined stress test library
  const {
    data: stressTestsEnvelope,
    isLoading: stressLoading,
    refetch,
  } = useQuery({
    queryKey: ['predefined-stress-tests', selectedPortfolioId],
    queryFn: () => scenarioApi.getPredefinedStressTests(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  // Custom Scenario Mutation
  const customScenarioMutation = useMutation({
    mutationFn: () =>
      scenarioApi.runScenario(selectedPortfolioId!, {
        scenarioName: 'Custom Analyst Macro Shock',
        marketDropPercent: customMarketDrop / 100,
        interestRateBps: customRateShockBps,
        volatilitySpikePercent: customVolSpike / 100,
      }),
  });

  if (!selectedPortfolioId) {
    return (
      <EmptyState
        icon={<Flame className="w-8 h-8 text-fin-amber" />}
        title="No Portfolio Selected"
        description="Select an active portfolio to simulate macro distress shocks and historical crises."
      />
    );
  }

  const defaultScenarios: StressTestResult[] = [
    {
      scenarioName: '2008 Global Financial Crisis',
      category: 'HISTORICAL',
      portfolioImpactPercent: -0.325,
      estimatedLoss: (valuation?.totalValue || 1500000) * -0.325,
      severity: 'SEVERE',
      description: 'Lehman collapse, liquidity freeze, banking crisis (-38% broad market drop)',
      assetImpacts: { RELIANCE: -0.28, TCS: -0.24, NIFTYBEES: -0.35 },
    },
    {
      scenarioName: '2020 COVID-19 Liquidity Shock',
      category: 'HISTORICAL',
      portfolioImpactPercent: -0.264,
      estimatedLoss: (valuation?.totalValue || 1500000) * -0.264,
      severity: 'SEVERE',
      description: 'Pandemic lockdowns, extreme rapid volatility spike across all asset classes',
      assetImpacts: { RELIANCE: -0.22, INFY: -0.19, NIFTYBEES: -0.28 },
    },
    {
      scenarioName: '2022 RBI & Fed Rate Shock Cycle',
      category: 'HISTORICAL',
      portfolioImpactPercent: -0.218,
      estimatedLoss: (valuation?.totalValue || 1500000) * -0.218,
      severity: 'MODERATE',
      description: 'Aggressive central bank rate hikes and valuation compression for tech & growth assets',
      assetImpacts: { TCS: -0.24, INFY: -0.26, HDFCBANK: -0.16 },
    },
    {
      scenarioName: 'Stagflationary Spike (Crude Oil $120+, Inflation 8%)',
      category: 'MACRO',
      portfolioImpactPercent: -0.142,
      estimatedLoss: (valuation?.totalValue || 1500000) * -0.142,
      severity: 'MODERATE',
      description: 'Crude commodity surge accompanied by domestic inflation and RBI tightening',
      assetImpacts: { RELIANCE: -0.08, TATAMOTORS: -0.18, NIFTYBEES: -0.14 },
    },
  ];

  const results = stressTestsEnvelope?.result || defaultScenarios;
  const customResult = customScenarioMutation.data;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <Flame className="w-5 h-5 text-fin-amber" />
            Market Crash Testing (What If the Market Crashes?)
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Test how your money would survive famous market crashes like COVID-19 or the 2008 crisis, or simulate your own market shock
          </p>
        </div>

        <Button
          variant="secondary"
          size="sm"
          onClick={() => refetch()}
          leftIcon={<Play className="w-3.5 h-3.5" />}
        >
          Re-test Past Crises
        </Button>
      </div>

      {/* Custom Shock Interactive Builder */}
      <Card
        title="Custom Market Crash Simulator"
        subtitle="Test what happens to your savings if the stock market suddenly drops by 10%, 15%, or 20%"
      >
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Input
            label="Stock Market Drop (%)"
            type="number"
            value={customMarketDrop}
            onChange={(e) => setCustomMarketDrop(Number(e.target.value))}
            helperText="e.g. -15 for a 15% Nifty market drop"
          />

          <Input
            label="RBI Interest Rate Hike (bps)"
            type="number"
            value={customRateShockBps}
            onChange={(e) => setCustomRateShockBps(Number(e.target.value))}
            helperText="e.g. +100 for a 1% rate increase"
          />

          <Input
            label="Market Panic (VIX) Surge (%)"
            type="number"
            value={customVolSpike}
            onChange={(e) => setCustomVolSpike(Number(e.target.value))}
            helperText="e.g. +50 for a sudden fear spike"
          />
        </div>

        <div className="mt-4 flex items-center justify-between pt-3 border-t border-fin-border">
          <div className="text-xs text-fin-muted">
            {customResult ? (
              <span className="text-fin-red font-mono font-semibold">
                Estimated Impact: {formatPercent(customResult.portfolioImpactPercent)} (
                {formatCurrency(customResult.estimatedLoss, valuation?.baseCurrency || 'INR')})
              </span>
            ) : (
              'Simulate how your current holdings would withstand this tailored shock'
            )}
          </div>

          <Button
            variant="primary"
            size="sm"
            onClick={() => customScenarioMutation.mutate()}
            isLoading={customScenarioMutation.isPending}
            leftIcon={<Flame className="w-3.5 h-3.5" />}
          >
            Test Crash Impact
          </Button>
        </div>
      </Card>

      {/* Historical Crisis Impact Ledger */}
      <Card
        title="Past Market Crises Replay"
        subtitle="See how your current portfolio of stocks would have behaved in famous past crises"
        noPadding
      >
        <div className="overflow-x-auto">
          <table className="w-full min-w-[720px] text-left border-collapse">
            <thead>
              <tr className="border-b border-fin-border bg-fin-panel/50 text-[11px] font-mono uppercase tracking-wider text-fin-muted">
                <th className="px-5 py-3 font-semibold">Scenario Event</th>
                <th className="px-5 py-3 font-semibold">Category</th>
                <th className="px-5 py-3 font-semibold">Severity</th>
                <th className="px-5 py-3 font-semibold text-right">Portfolio Drawdown</th>
                <th className="px-5 py-3 font-semibold text-right">Estimated Capital Loss</th>
                <th className="px-5 py-3 font-semibold">Historical Context</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-fin-border/50 text-xs">
              {results.map((sc, idx) => {
                const severityVariant =
                  sc.severity === 'CATASTROPHIC' || sc.severity === 'SEVERE'
                    ? 'danger'
                    : sc.severity === 'MODERATE'
                    ? 'warning'
                    : 'info';

                return (
                  <tr key={idx} className="hover:bg-fin-panel/40 transition-colors">
                    <td className="px-5 py-3 font-semibold text-white">
                      {sc.scenarioName}
                    </td>
                    <td className="px-5 py-3">
                      <Badge variant="default" size="sm">
                        {sc.category}
                      </Badge>
                    </td>
                    <td className="px-5 py-3">
                      <Badge variant={severityVariant as any} size="sm">
                        {sc.severity}
                      </Badge>
                    </td>
                    <td className="px-5 py-3 font-mono font-bold text-right text-fin-red">
                      {formatPercent(sc.portfolioImpactPercent)}
                    </td>
                    <td className="px-5 py-3 font-mono font-semibold text-right text-fin-red">
                      {formatCurrency(sc.estimatedLoss, valuation?.baseCurrency || 'INR')}
                    </td>
                    <td className="px-5 py-3 text-fin-muted max-w-sm truncate">
                      {sc.description}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Educational Guide Card */}
      <div className="p-5 rounded-xl bg-fin-card border border-fin-border">
        <h3 className="text-sm font-bold text-white mb-2 flex items-center gap-2">
          <span>💡 Why Run Stress Testing?</span>
        </h3>
        <p className="text-xs text-fin-muted leading-relaxed">
          Stress tests simulate how unexpected crises—like a global liquidity crunch, crude oil spike, or sudden interest rate hike—would impact your equities. If a scenario causes too high a loss for your comfort, consider hedging or allocating a portion into <strong className="text-white">GOLDBEES</strong> (Gold ETF) or high-quality defensive bluechips.
        </p>
      </div>
    </div>
  );
};
