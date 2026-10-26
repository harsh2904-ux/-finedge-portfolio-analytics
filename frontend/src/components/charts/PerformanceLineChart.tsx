import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from '../common/Card';
import { Skeleton } from '../common/Skeleton';
import { useChartTheme } from '../../utils/themeColors';

interface SeriesDataPoint {
  date: string;
  portfolio: number;
  benchmark?: number;
}

interface PerformanceLineChartProps {
  data?: SeriesDataPoint[];
  portfolioName?: string;
  benchmarkName?: string;
  isLoading?: boolean;
  lookback?: string;
  onLookbackChange?: (lookback: string) => void;
  height?: number;
}

export const PerformanceLineChart: React.FC<PerformanceLineChartProps> = ({
  data = [],
  portfolioName = 'Portfolio',
  benchmarkName = 'Benchmark (NIFTYBEES)',
  isLoading = false,
  lookback = '1Y',
  onLookbackChange,
  height = 360,
}) => {
  const { isDark, mutedColor, axisLineColor, splitLineColor, tooltipBg, tooltipBorder, tooltipTextColor } = useChartTheme();
  const lookbackOptions = ['1M', '3M', '6M', '1Y', '3Y', '5Y'];

  if (isLoading) {
    return (
      <Card
        title="Cumulative Performance"
        subtitle="Historical portfolio growth vs benchmark"
      >
        <Skeleton height={height} />
      </Card>
    );
  }

  // Fallback demo points if data is empty
  const dates = data.length > 0 ? data.map((d) => d.date) : ['2023-10', '2024-01', '2024-04', '2024-07', '2024-10'];
  const portfolioReturns = data.length > 0 ? data.map((d) => (d.portfolio * 100).toFixed(2)) : ['0.0', '4.2', '9.5', '14.8', '19.2'];
  const benchmarkReturns = data.length > 0 ? data.map((d) => (d.benchmark !== undefined ? (d.benchmark * 100).toFixed(2) : '0.0')) : ['0.0', '3.1', '6.8', '11.2', '14.5'];

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipTextColor, fontSize: 12 },
      formatter: (params: any) => {
        let content = `<div class="font-mono text-xs pb-1 mb-1 border-b ${isDark ? 'border-slate-700' : 'border-slate-200'}">${params[0].name}</div>`;
        params.forEach((item: any) => {
          content += `<div class="flex items-center justify-between gap-4 text-xs py-0.5">
            <span style="color:${item.color}">${item.seriesName}:</span>
            <span class="font-mono font-semibold">${item.value}%</span>
          </div>`;
        });
        return content;
      },
    },
    legend: {
      data: [portfolioName, benchmarkName],
      top: 0,
      right: 0,
      textStyle: { color: mutedColor, fontSize: 11 },
      icon: 'roundRect',
    },
    grid: {
      left: '1%',
      right: '2%',
      bottom: '3%',
      top: '12%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: mutedColor, fontSize: 10, rotate: 0 },
      splitLine: { show: false },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: {
        color: mutedColor,
        fontSize: 10,
        formatter: '{value}%',
      },
      splitLine: {
        lineStyle: { color: splitLineColor, type: 'dashed' },
      },
    },
    series: [
      {
        name: portfolioName,
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: portfolioReturns,
        lineStyle: { width: 2.5, color: '#3B82F6' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(59, 130, 246, 0.35)' },
              { offset: 1, color: 'rgba(59, 130, 246, 0.0)' },
            ],
          },
        },
      },
      {
        name: benchmarkName,
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: benchmarkReturns,
        lineStyle: { width: 1.8, color: '#94A3B8', type: 'dashed' },
      },
    ],
  };

  return (
    <Card
      title="Cumulative Performance"
      subtitle="Cumulative percentage return vs selected benchmark"
      action={
        onLookbackChange && (
          <div className="flex items-center gap-1 bg-fin-panel border border-fin-border p-0.5 rounded-lg">
            {lookbackOptions.map((opt) => (
              <button
                key={opt}
                onClick={() => onLookbackChange(opt)}
                className={`px-2 py-1 text-xs rounded font-medium transition-colors ${
                  lookback === opt
                    ? 'bg-fin-blue text-white shadow-sm'
                    : 'text-fin-muted hover:text-fin-text'
                }`}
              >
                {opt}
              </button>
            ))}
          </div>
        )
      }
    >
      <div style={{ height }}>
        <ReactECharts option={option} style={{ height: '100%', width: '100%' }} notMerge={true} />
      </div>
    </Card>
  );
};
