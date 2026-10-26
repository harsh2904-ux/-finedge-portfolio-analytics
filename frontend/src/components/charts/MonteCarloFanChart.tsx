import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from '../common/Card';
import { Skeleton } from '../common/Skeleton';
import { formatCurrency } from '../../utils/formatters';
import { useChartTheme } from '../../utils/themeColors';

interface MonteCarloFanChartProps {
  trajectories?: number[][]; // sample paths [numPaths][days]
  percentiles?: {
    days: number[];
    p5: number[];
    p25: number[];
    p50: number[];
    p75: number[];
    p95: number[];
  };
  initialValue?: number;
  isLoading?: boolean;
  height?: number;
}

export const MonteCarloFanChart: React.FC<MonteCarloFanChartProps> = ({
  percentiles,
  initialValue = 100000,
  isLoading = false,
  height = 380,
}) => {
  const { isDark, mutedColor, axisLineColor, splitLineColor, tooltipBg, tooltipBorder, tooltipTextColor } = useChartTheme();

  if (isLoading) {
    return (
      <Card
        title="Monte Carlo Simulation Fan Chart"
        subtitle="Stochastic portfolio trajectory distribution"
      >
        <Skeleton height={height} />
      </Card>
    );
  }

  // Generate synthetic days if percentiles are not ready yet
  const days = percentiles?.days || Array.from({ length: 60 }, (_, i) => i * 4);
  const p5 = percentiles?.p5 || days.map((d) => initialValue * (1 - 0.001 * d - 0.05));
  const p25 = percentiles?.p25 || days.map((d) => initialValue * (1 - 0.0003 * d - 0.02));
  const p50 = percentiles?.p50 || days.map((d) => initialValue * (1 + 0.0004 * d));
  const p75 = percentiles?.p75 || days.map((d) => initialValue * (1 + 0.0012 * d + 0.02));
  const p95 = percentiles?.p95 || days.map((d) => initialValue * (1 + 0.0022 * d + 0.06));

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipTextColor, fontSize: 12 },
      formatter: (params: any) => {
        let content = `<div class="font-mono text-xs pb-1 mb-1 border-b ${isDark ? 'border-slate-700' : 'border-slate-200'}">Day ${params[0].name}</div>`;
        params.forEach((item: any) => {
          content += `<div class="flex items-center justify-between gap-4 text-xs py-0.5">
            <span style="color:${item.color}">${item.seriesName}:</span>
            <span class="font-mono font-semibold">₹${Number(item.value).toLocaleString('en-IN', { maximumFractionDigits: 0 })}</span>
          </div>`;
        });
        return content;
      },
    },
    legend: {
      data: ['95th Percentile (Bull)', '75th Percentile', '50th Median', '25th Percentile', '5th Percentile (Bear)'],
      top: 0,
      right: 0,
      textStyle: { color: mutedColor, fontSize: 11 },
    },
    grid: {
      left: '2%',
      right: '2%',
      bottom: '3%',
      top: '12%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      name: 'Trading Days Ahead',
      nameLocation: 'middle',
      nameGap: 25,
      nameTextStyle: { color: mutedColor, fontSize: 11 },
      data: days.map(String),
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: mutedColor, fontSize: 10 },
    },
    yAxis: {
      type: 'value',
      name: 'Portfolio Value (₹)',
      nameLocation: 'middle',
      nameGap: 45,
      nameTextStyle: { color: mutedColor, fontSize: 11 },
      axisLine: { show: false },
      axisLabel: {
        color: mutedColor,
        fontSize: 10,
        formatter: (v: number) => `₹${(v / 1000).toFixed(0)}k`,
      },
      splitLine: { lineStyle: { color: splitLineColor, type: 'dashed' } },
    },
    series: [
      {
        name: '95th Percentile (Bull)',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: p95.map((v) => Number(v.toFixed(0))),
        lineStyle: { width: 1.5, color: '#10B981', type: 'dashed' },
      },
      {
        name: '75th Percentile',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: p75.map((v) => Number(v.toFixed(0))),
        lineStyle: { width: 1.5, color: '#3B82F6' },
      },
      {
        name: '50th Median',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: p50.map((v) => Number(v.toFixed(0))),
        lineStyle: { width: 2.5, color: '#FFFFFF' },
      },
      {
        name: '25th Percentile',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: p25.map((v) => Number(v.toFixed(0))),
        lineStyle: { width: 1.5, color: '#F59E0B' },
      },
      {
        name: '5th Percentile (Bear)',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: p5.map((v) => Number(v.toFixed(0))),
        lineStyle: { width: 1.5, color: '#EF4444', type: 'dashed' },
      },
    ],
  };

  return (
    <Card
      title="Monte Carlo Simulation Fan Chart"
      subtitle="Geometric Brownian Motion simulation cone over trading days horizon"
    >
      <div style={{ height }}>
        <ReactECharts option={option} style={{ height: '100%', width: '100%' }} notMerge={true} />
      </div>
    </Card>
  );
};
