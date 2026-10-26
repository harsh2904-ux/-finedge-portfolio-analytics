import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from '../common/Card';
import { Skeleton } from '../common/Skeleton';
import { useChartTheme } from '../../utils/themeColors';

interface DrawdownPoint {
  date: string;
  drawdown: number; // e.g. -0.042 for -4.2%
}

interface DrawdownChartProps {
  data?: DrawdownPoint[];
  maxDrawdown?: number; // e.g. -0.158
  isLoading?: boolean;
  height?: number;
}

export const DrawdownChart: React.FC<DrawdownChartProps> = ({
  data = [],
  maxDrawdown,
  isLoading = false,
  height = 260,
}) => {
  const { isDark, mutedColor, axisLineColor, splitLineColor, tooltipBg, tooltipBorder, tooltipTextColor } = useChartTheme();

  if (isLoading) {
    return (
      <Card title="Historical Drawdown (Underwater)" subtitle="Peak-to-trough decline over time">
        <Skeleton height={height} />
      </Card>
    );
  }

  const dates = data.map((d) => d.date);
  const values = data.map((d) => (d.drawdown * 100).toFixed(2));

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipTextColor, fontSize: 12 },
      formatter: (params: any) => {
        const item = params[0];
        return `<div class="font-mono text-xs">
          <div class="${isDark ? 'text-slate-400' : 'text-slate-500'} mb-1">${item.name}</div>
          <div class="flex items-center justify-between gap-4">
            <span class="text-fin-red font-semibold">Drawdown:</span>
            <span class="${isDark ? 'text-white' : 'text-slate-900'} font-bold">${item.value}%</span>
          </div>
        </div>`;
      },
    },
    grid: {
      left: '1%',
      right: '2%',
      bottom: '3%',
      top: '10%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: dates.length > 0 ? dates : ['2023-10', '2024-01', '2024-04', '2024-07', '2024-10'],
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: mutedColor, fontSize: 10 },
      splitLine: { show: false },
    },
    yAxis: {
      type: 'value',
      max: 0,
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
        name: 'Drawdown',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: values.length > 0 ? values : ['0.0', '-1.5', '-4.2', '-2.1', '-6.8'],
        lineStyle: { width: 1.5, color: '#EF4444' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(239, 68, 68, 0.4)' },
              { offset: 1, color: 'rgba(239, 68, 68, 0.05)' },
            ],
          },
        },
      },
    ],
  };

  return (
    <Card
      title="Historical Drawdown (Underwater)"
      subtitle="Peak-to-trough decline over time"
      action={
        maxDrawdown !== undefined && (
          <span className="text-xs font-mono text-fin-red font-semibold bg-red-500/10 px-2 py-0.5 rounded border border-red-500/20">
            Max: {(maxDrawdown * 100).toFixed(2)}%
          </span>
        )
      }
    >
      <div style={{ height }}>
        <ReactECharts option={option} style={{ height: '100%', width: '100%' }} notMerge={true} />
      </div>
    </Card>
  );
};
