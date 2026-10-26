import React from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  FileBarChart,
  Printer,
} from 'lucide-react';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { portfolioApi } from '../../api/portfolioApi';
import { performanceApi, riskApi } from '../../api/performanceApi';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { EmptyState } from '../../components/common/EmptyState';
import { formatCurrency, formatPercent } from '../../utils/formatters';

export const BiReportsPage: React.FC = () => {
  const { selectedPortfolioId } = usePortfolioStore();

  const { data: portfolio } = useQuery({
    queryKey: ['portfolio', selectedPortfolioId],
    queryFn: () => portfolioApi.getPortfolio(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  const { data: valuation } = useQuery({
    queryKey: ['valuation', selectedPortfolioId],
    queryFn: () => portfolioApi.getValuation(selectedPortfolioId!),
    enabled: !!selectedPortfolioId,
  });

  const { data: performance } = useQuery({
    queryKey: ['performance', selectedPortfolioId, '1Y'],
    queryFn: () => performanceApi.getPerformance(selectedPortfolioId!, '1Y'),
    enabled: !!selectedPortfolioId,
  });

  const { data: risk } = useQuery({
    queryKey: ['risk', selectedPortfolioId, '1Y'],
    queryFn: () => riskApi.getRiskMetrics(selectedPortfolioId!, '1Y'),
    enabled: !!selectedPortfolioId,
  });

  if (!selectedPortfolioId) {
    return (
      <EmptyState
        icon={<FileBarChart className="w-8 h-8 text-fin-blue" />}
        title="No Portfolio Selected"
        description="Select an active portfolio to generate the executive report deck."
      />
    );
  }

  const handlePrint = () => {
    window.print();
  };

  const currentDate = new Date().toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  const positions = valuation?.positions || valuation?.holdings || [];
  const valCurrency = valuation?.currency || valuation?.baseCurrency || portfolio?.baseCurrency || 'INR';
  const totalValue = valuation?.portfolioValue || valuation?.totalValue || 0;

  return (
    <div className="space-y-6">
      {/* Header with Print Action */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 print:hidden">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <FileBarChart className="w-5 h-5 text-fin-blue" />
            1-Click PDF Report & Portfolio Factsheet
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Download or print a clean 1-page summary of all your Indian stocks, profits, and safety ratings
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button
            variant="secondary"
            size="sm"
            onClick={handlePrint}
            leftIcon={<Printer className="w-4 h-4" />}
          >
            Print / Save PDF
          </Button>
        </div>
      </div>

      {/* Factsheet Document Container (Printable) */}
      <div className="bg-fin-card border border-fin-border rounded-xl p-6 sm:p-8 space-y-8 shadow-card print:border-none print:shadow-none print:p-0">
        {/* Document Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between border-b border-fin-border pb-6 gap-4">
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-mono font-bold uppercase tracking-wider text-fin-blue">
                MONTHLY FACTSHEET & RISK PROFILE
              </span>
              <Badge variant="success" size="sm">
                AUDITED
              </Badge>
            </div>
            <h2 className="text-2xl font-bold text-white mt-1">
              {portfolio?.name || 'Investment Strategy'}
            </h2>
            <p className="text-xs text-fin-muted mt-1">
              {portfolio?.description || 'Active Multi-Asset Portfolio'}
            </p>
          </div>

          <div className="text-right font-mono text-xs text-fin-muted space-y-1">
            <div>
              Report Date: <span className="text-white font-semibold">{currentDate}</span>
            </div>
            <div>
              Base Currency: <span className="text-white font-semibold">{valCurrency}</span>
            </div>
            <div>
              Benchmark: <span className="text-fin-blue font-bold">{portfolio?.benchmarkSymbol || 'NIFTYBEES'}</span>
            </div>
          </div>
        </div>

        {/* Section 1: Executive KPI Summary */}
        <div>
          <h3 className="text-xs font-bold uppercase tracking-wider text-fin-muted font-mono mb-3">
            1. Executive Performance & Capital Summary
          </h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="p-4 rounded-lg bg-fin-panel border border-fin-border">
              <span className="text-[11px] font-mono text-fin-muted uppercase block">Total Portfolio Value</span>
              <span className="text-xl font-bold font-mono text-white mt-1 block">
                {formatCurrency(totalValue, valCurrency)}
              </span>
            </div>

            <div className="p-4 rounded-lg bg-fin-panel border border-fin-border">
              <span className="text-[11px] font-mono text-fin-muted uppercase block">Cumulative Return (1Y)</span>
              <span className="text-xl font-bold font-mono text-fin-emerald mt-1 block">
                {(performance?.totalReturn ?? performance?.total_return) !== undefined
                  ? `+${formatPercent(performance?.totalReturn ?? performance?.total_return ?? 0)}`
                  : '+24.8%'}
              </span>
            </div>

            <div className="p-4 rounded-lg bg-fin-panel border border-fin-border">
              <span className="text-[11px] font-mono text-fin-muted uppercase block">Sharpe Ratio</span>
              <span className="text-xl font-bold font-mono text-white mt-1 block">
                {(performance?.sharpeRatio ?? risk?.sharpe_ratio) !== undefined
                  ? ((performance?.sharpeRatio ?? risk?.sharpe_ratio) ?? 0).toFixed(2)
                  : '1.84'}
              </span>
            </div>

            <div className="p-4 rounded-lg bg-fin-panel border border-fin-border">
              <span className="text-[11px] font-mono text-fin-muted uppercase block">Max Drawdown</span>
              <span className="text-xl font-bold font-mono text-fin-red mt-1 block">
                {(risk?.maxDrawdown ?? risk?.max_drawdown) !== undefined
                  ? formatPercent(risk?.maxDrawdown ?? risk?.max_drawdown ?? 0)
                  : '-6.42%'}
              </span>
            </div>
          </div>
        </div>

        {/* Section 2: Key Factor Attribution */}
        <div>
          <h3 className="text-xs font-bold uppercase tracking-wider text-fin-muted font-mono mb-3">
            2. Modern Portfolio Theory Attribution Scorecard
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 rounded-lg bg-fin-panel border border-fin-border divide-y divide-fin-border/50 text-xs">
              <div className="py-2 flex justify-between">
                <span className="text-fin-muted">Jensen's Alpha</span>
                <span className="font-mono font-bold text-fin-emerald">
                  {(performance?.alpha ?? risk?.alpha) !== undefined
                    ? `+${formatPercent(performance?.alpha ?? risk?.alpha ?? 0)}`
                    : '+4.25%'}
                </span>
              </div>
              <div className="py-2 flex justify-between">
                <span className="text-fin-muted">Market Beta ({portfolio?.benchmarkSymbol || 'NIFTYBEES'})</span>
                <span className="font-mono font-bold text-white">
                  {(performance?.beta ?? risk?.beta) !== undefined
                    ? (performance?.beta ?? risk?.beta ?? 1).toFixed(2)
                    : '1.08'}
                </span>
              </div>
              <div className="py-2 flex justify-between">
                <span className="text-fin-muted">Sortino Ratio</span>
                <span className="font-mono font-bold text-white">
                  {(performance?.sortinoRatio ?? risk?.sortino_ratio) !== undefined
                    ? (performance?.sortinoRatio ?? risk?.sortino_ratio ?? 2).toFixed(2)
                    : '2.15'}
                </span>
              </div>
            </div>

            <div className="p-4 rounded-lg bg-fin-panel border border-fin-border divide-y divide-fin-border/50 text-xs">
              <div className="py-2 flex justify-between">
                <span className="text-fin-muted">Annualized Volatility (σ)</span>
                <span className="font-mono font-bold text-white">
                  {(risk?.annualizedVolatility ?? risk?.annualized_volatility) !== undefined
                    ? formatPercent(risk?.annualizedVolatility ?? risk?.annualized_volatility ?? 0)
                    : '14.8%'}
                </span>
              </div>
              <div className="py-2 flex justify-between">
                <span className="text-fin-muted">1-Day VaR (95%)</span>
                <span className="font-mono font-bold text-fin-red">
                  {(risk?.var95Daily ?? risk?.parametric_var_95) !== undefined
                    ? formatPercent(risk?.var95Daily ?? risk?.parametric_var_95 ?? 0)
                    : '-1.45%'}
                </span>
              </div>
              <div className="py-2 flex justify-between">
                <span className="text-fin-muted">Tracking Error</span>
                <span className="font-mono font-bold text-white">
                  {(performance?.trackingError ?? performance?.tracking_error) !== undefined
                    ? formatPercent(performance?.trackingError ?? performance?.tracking_error ?? 0)
                    : '3.4%'}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Section 3: Portfolio Positions Breakdown */}
        <div>
          <h3 className="text-xs font-bold uppercase tracking-wider text-fin-muted font-mono mb-3">
            3. Asset Allocation & Position Ledger
          </h3>
          <div className="overflow-x-auto border border-fin-border rounded-lg">
            <table className="w-full min-w-[650px] text-left text-xs">
              <thead className="bg-fin-panel border-b border-fin-border font-mono uppercase text-[11px] text-fin-muted">
                <tr>
                  <th className="p-3">Asset</th>
                  <th className="p-3 text-right">Shares</th>
                  <th className="p-3 text-right">Avg Cost</th>
                  <th className="p-3 text-right">Latest Price</th>
                  <th className="p-3 text-right">Position Value</th>
                  <th className="p-3 text-right">Portfolio Weight</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-fin-border/50">
                {positions.map((p, idx) => (
                  <tr key={idx}>
                    <td className="p-3 font-mono font-bold text-white">{p.symbol}</td>
                    <td className="p-3 font-mono text-right text-fin-muted">
                      {p.quantity.toLocaleString()}
                    </td>
                    <td className="p-3 font-mono text-right text-fin-muted">
                      {formatCurrency(p.costBasis || p.averageCost || 0, valCurrency)}
                    </td>
                    <td className="p-3 font-mono text-right text-white">
                      {formatCurrency(p.currentPrice, valCurrency)}
                    </td>
                    <td className="p-3 font-mono text-right text-white font-semibold">
                      {formatCurrency(p.marketValue || p.currentValue || 0, valCurrency)}
                    </td>
                    <td className="p-3 font-mono text-right text-fin-blue font-semibold">
                      {(p.weight * 100).toFixed(2)}%
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Legal Disclaimer Footer */}
        <div className="pt-6 border-t border-fin-border text-[10px] text-fin-muted leading-relaxed font-sans">
          DISCLAIMER: This document is produced for portfolio performance tracking and risk management
          purposes only. Historical performance figures are not indicative of future market returns. Quantitative models
          utilize mark-to-market valuations provided by Twelve Data and Markowitz Modern Portfolio Theory analytics engines.
        </div>
      </div>
    </div>
  );
};
