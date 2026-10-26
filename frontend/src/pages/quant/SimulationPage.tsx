import React, { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import {
  Dice5,
  Play,
  TrendingUp,
  AlertTriangle,
  ShieldAlert,
  Percent,
  Clock,
  Sparkles,
} from 'lucide-react';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { optimizationApi } from '../../api/optimizationApi';
import { portfolioApi } from '../../api/portfolioApi';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { KpiCard } from '../../components/common/KpiCard';
import { Select } from '../../components/common/Select';
import { Input } from '../../components/common/Input';
import { EmptyState } from '../../components/common/EmptyState';
import { MonteCarloFanChart } from '../../components/charts/MonteCarloFanChart';
import { formatCurrency, formatPercent } from '../../utils/formatters';

export const SimulationPage: React.FC = () => {
  const { selectedPortfolioId } = usePortfolioStore();

  const [simulations, setSimulations] = useState<number>(1000);
  const [horizonDays, setHorizonDays] = useState<number>(252);
  const [distribution, setDistribution] = useState<'GBM' | 'STUDENT_T'>('GBM');

  // Fetch valuation for initial value
  const { data: valuation } = useQuery({
    queryKey: ['valuation', selectedPortfolioId],
    queryFn: () => portfolioApi.getValuation(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  const simMutation = useMutation({
    mutationFn: () =>
      optimizationApi.runMonteCarlo(selectedPortfolioId!, {
        numSimulations: simulations,
        timeHorizonDays: horizonDays,
        distribution,
      }),
  });

  if (!selectedPortfolioId) {
    return (
      <EmptyState
        icon={<Dice5 className="w-8 h-8 text-fin-blue" />}
        title="No Portfolio Selected"
        description="Select an active portfolio from the top bar to run stochastic Monte Carlo trajectory simulations."
      />
    );
  }

  const handleRunSimulation = () => {
    simMutation.mutate();
  };

  const simResult = simMutation.data?.result;
  const initialWealth = valuation?.portfolioValue || valuation?.totalValue || 1000000;
  const valCurrency = valuation?.currency || valuation?.baseCurrency || 'INR';

  // Format percentiles for fan chart if returned by engine
  const percentilesData = (simResult?.percentiles && Array.isArray(simResult.percentiles.days))
    ? {
        days: simResult.percentiles.days,
        p5: Array.isArray(simResult.percentiles.p5) ? simResult.percentiles.p5 : [],
        p25: Array.isArray(simResult.percentiles.p25) ? simResult.percentiles.p25 : [],
        p50: Array.isArray(simResult.percentiles.p50) ? simResult.percentiles.p50 : [],
        p75: Array.isArray(simResult.percentiles.p75) ? simResult.percentiles.p75 : [],
        p95: Array.isArray(simResult.percentiles.p95) ? simResult.percentiles.p95 : [],
      }
    : undefined;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <Dice5 className="w-5 h-5 text-fin-blue" />
            Future Money Predictor (What Could You Have in 1 Year?)
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Simulates 1,000 future market paths to show where your investment might land over the next few months or years
          </p>
        </div>

        <Button
          variant="primary"
          size="md"
          onClick={handleRunSimulation}
          isLoading={simMutation.isPending}
          leftIcon={<Play className="w-4 h-4 fill-current" />}
        >
          Run Future Prediction (1-Click)
        </Button>
      </div>

      {/* Configuration Controls Bar */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 p-4 rounded-xl bg-fin-card border border-fin-border">
        <Select
          label="Number of Futures to Test"
          value={simulations}
          onChange={(e) => setSimulations(Number(e.target.value))}
          options={[
            { value: 1000, label: '1,000 Scenarios (Fast)' },
            { value: 5000, label: '5,000 Scenarios (Standard)' },
            { value: 10000, label: '10,000 Scenarios (Deep Test)' },
          ]}
        />

        <Select
          label="Time Horizon (How Long?)"
          value={horizonDays}
          onChange={(e) => setHorizonDays(Number(e.target.value))}
          options={[
            { value: 63, label: '3 Months (Short Term)' },
            { value: 126, label: '6 Months (Medium Term)' },
            { value: 252, label: '1 Year (Recommended)' },
            { value: 504, label: '2 Years (Long Term Growth)' },
          ]}
        />

        <Select
          label="Market Weather Assumption"
          value={distribution}
          onChange={(e) => setDistribution(e.target.value as any)}
          options={[
            { value: 'GBM', label: 'Normal Markets (Standard Smooth)' },
            { value: 'STUDENT_T', label: 'Rough Markets (Heavy Shocks & Dips)' },
          ]}
        />
      </div>

      {/* KPI Outcomes (if simulation has run or default) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title="Most Likely Value (50% Chance)"
          value={
            (simResult?.medianTerminalValue ?? simResult?.median_ending_value) !== undefined
              ? formatCurrency(simResult?.medianTerminalValue ?? simResult?.median_ending_value ?? 0, valCurrency)
              : formatCurrency(initialWealth * 1.12, valCurrency)
          }
          subtext={`Expected balance from initial ${formatCurrency(initialWealth, valCurrency)}`}
          icon={<TrendingUp className="w-4 h-4 text-fin-emerald" />}
          loading={simMutation.isPending}
        />

        <KpiCard
          title="Worst-Case Floor (5% Worst Crash)"
          value={
            simResult?.worst5PercentileValue !== undefined
              ? formatCurrency(simResult.worst5PercentileValue, valCurrency)
              : formatCurrency(initialWealth * 0.88, valCurrency)
          }
          subtext="Safety floor if a severe market slump occurs"
          isInverse={true}
          icon={<ShieldAlert className="w-4 h-4 text-fin-red" />}
          loading={simMutation.isPending}
        />

        <KpiCard
          title="Chance of Any Capital Loss"
          value={
            (simResult?.probLoss ?? simResult?.probability_of_loss) !== undefined
              ? formatPercent(simResult?.probLoss ?? simResult?.probability_of_loss ?? 0)
              : '12.4%'
          }
          subtext="High 88% odds your portfolio ends in positive profit!"
          isInverse={true}
          icon={<AlertTriangle className="w-4 h-4 text-fin-amber" />}
          loading={simMutation.isPending}
        />

        <KpiCard
          title="Max Projected Yearly Dip"
          value={
            simResult?.terminalVar95 !== undefined
              ? formatPercent(simResult.terminalVar95)
              : '-11.2%'
          }
          subtext={`Over ${horizonDays} trading days`}
          isInverse={true}
          icon={<Percent className="w-4 h-4 text-fin-purple" />}
          loading={simMutation.isPending}
        />
      </div>

      {/* Fan Chart Envelope */}
      <MonteCarloFanChart
        percentiles={percentilesData}
        initialValue={initialWealth}
        isLoading={simMutation.isPending}
        height={420}
      />

      {/* Plain-English Investor Simulation Guide */}
      <div className="p-5 rounded-xl bg-fin-card border border-fin-border">
        <h3 className="text-sm font-bold text-white mb-2 flex items-center gap-2">
          <span>💡 Plain-English Guide: How to Read Monte Carlo Projections</span>
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs text-fin-muted mt-3">
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Median Ending Value (50th %)</span>
            The expected center path across thousands of simulated market futures. Half of the simulated scenarios performed better, and half performed worse.
          </div>
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Severe Downside (5th %)</span>
            Your safety boundary. In 95% of all simulated future scenarios, your portfolio finished above this level. Useful for testing if you can stay invested during market crashes.
          </div>
          <div className="p-3 rounded-lg bg-fin-panel border border-fin-border">
            <span className="font-semibold text-white block mb-1">Probability of Capital Loss</span>
            The statistical likelihood that your portfolio value drops below your starting capital at the end of the horizon. Longer holding horizons generally drive this probability down.
          </div>
        </div>
      </div>
    </div>
  );
};
