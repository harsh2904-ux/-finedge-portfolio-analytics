import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from '../common/Card';
import { Skeleton } from '../common/Skeleton';
import { useChartTheme } from '../../utils/themeColors';

interface CorrelationHeatmapProps {
  symbols: string[];
  matrix: number[][]; // N x N matrix
  isLoading?: boolean;
  height?: number;
}

export const CorrelationHeatmap: React.FC<CorrelationHeatmapProps> = ({
  symbols = [],
  matrix = [],
  isLoading = false,
  height = 360,
}) => {
  const { isDark, mutedColor, textColor, axisLineColor, tooltipBg, tooltipBorder, tooltipTextColor } = useChartTheme();

  if (isLoading) {
    return (
      <Card title="Asset Correlation Matrix" subtitle="Pairwise return correlation (-1.0 to +1.0)">
        <Skeleton height={height} />
      </Card>
    );
  }

  // Format data as [colIndex, rowIndex, value]
  const data: [number, number, number][] = [];
  matrix.forEach((row, rIdx) => {
    row.forEach((val, cIdx) => {
      data.push([cIdx, rIdx, Number(val.toFixed(2))]);
    });
  });

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      position: 'top',
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipTextColor, fontSize: 12 },
      formatter: (params: any) => {
        const [c, r, val] = params.data;
        const sym1 = symbols[r];
        const sym2 = symbols[c];
        return `<div class="font-mono text-xs">
          <div class="${isDark ? 'text-slate-400' : 'text-slate-500'} mb-1">${sym1} ⟷ ${sym2}</div>
          <div>Correlation: <span class="font-bold ${isDark ? 'text-white' : 'text-slate-900'}">${val}</span></div>
        </div>`;
      },
    },
    grid: {
      left: '12%',
      right: '8%',
      top: '5%',
      bottom: '15%',
    },
    xAxis: {
      type: 'category',
      data: symbols,
      axisLabel: { color: textColor, fontSize: 11, fontWeight: 'bold' },
      axisLine: { lineStyle: { color: axisLineColor } },
      splitArea: { show: true, areaStyle: { color: ['transparent', isDark ? 'rgba(255,255,255,0.01)' : 'rgba(0,0,0,0.02)'] } },
    },
    yAxis: {
      type: 'category',
      data: symbols,
      axisLabel: { color: textColor, fontSize: 11, fontWeight: 'bold' },
      axisLine: { lineStyle: { color: axisLineColor } },
      splitArea: { show: true, areaStyle: { color: ['transparent', isDark ? 'rgba(255,255,255,0.01)' : 'rgba(0,0,0,0.02)'] } },
    },
    visualMap: {
      min: -1,
      max: 1,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '0%',
      itemWidth: 12,
      itemHeight: 120,
      textStyle: { color: mutedColor, fontSize: 10 },
      inRange: {
        color: isDark ? ['#1E3A8A', '#0F172A', '#10B981'] : ['#3B82F6', '#E2E8F0', '#10B981'], // Negative (blue), 0 (mid), positive (green)
      },
    },
    series: [
      {
        name: 'Correlation',
        type: 'heatmap',
        data: data,
        label: {
          show: true,
          color: '#FFFFFF',
          fontSize: 11,
          fontFamily: 'monospace',
          formatter: (params: any) => params.data[2].toFixed(2),
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            borderColor: '#FFFFFF',
            borderWidth: 1,
          },
        },
      },
    ],
  };

  return (
    <Card
      title="Asset Correlation Matrix"
      subtitle="Pairwise historical return correlation (-1.0 negative to +1.0 positive)"
    >
      <div style={{ height }}>
        {symbols.length > 0 ? (
          <ReactECharts option={option} style={{ height: '100%', width: '100%' }} notMerge={true} />
        ) : (
          <div className="flex items-center justify-center h-full text-fin-muted text-xs">
            Insufficient asset data for correlation analysis
          </div>
        )}
      </div>
    </Card>
  );
};
