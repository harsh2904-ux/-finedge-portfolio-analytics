import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from '../common/Card';
import { Skeleton } from '../common/Skeleton';
import { useChartTheme } from '../../utils/themeColors';
import type { FrontierPoint } from '../../types/optimization';

interface EfficientFrontierChartProps {
  frontierPoints?: FrontierPoint[];
  currentPoint?: { volatility: number; returnVal: number; sharpe: number };
  maxSharpePoint?: { volatility: number; returnVal: number; sharpe: number };
  minVolPoint?: { volatility: number; returnVal: number; sharpe: number };
  individualAssets?: { symbol: string; volatility: number; returnVal: number }[];
  isLoading?: boolean;
  height?: number;
}

export const EfficientFrontierChart: React.FC<EfficientFrontierChartProps> = ({
  frontierPoints = [],
  currentPoint,
  maxSharpePoint,
  minVolPoint,
  individualAssets = [],
  isLoading = false,
  height = 420,
}) => {
  const { isDark, mutedColor, axisLineColor, splitLineColor, tooltipBg, tooltipBorder, tooltipTextColor } = useChartTheme();

  if (isLoading) {
    return (
      <Card
        title="Markowitz Efficient Frontier"
        subtitle="Modern Portfolio Theory Risk-Return Envelope"
      >
        <Skeleton height={height} />
      </Card>
    );
  }

  // Curve data: [volatility * 100, return * 100]
  const curveData = frontierPoints.map((p) => [
    Number((p.volatility * 100).toFixed(2)),
    Number(((p.expectedReturn ?? p.expected_return ?? 0) * 100).toFixed(2)),
  ]);

  // Individual assets: [volatility * 100, return * 100, symbol]
  const assetScatterData = individualAssets.map((a) => [
    Number((a.volatility * 100).toFixed(2)),
    Number((a.returnVal * 100).toFixed(2)),
    a.symbol,
  ]);

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipTextColor, fontSize: 12 },
      formatter: (params: any) => {
        if (params.seriesType === 'scatter' && params.data[2]) {
          return `<div class="font-mono text-xs">
            <div class="font-bold ${isDark ? 'text-white' : 'text-slate-900'} mb-1">${params.data[2]}</div>
            <div class="${isDark ? 'text-slate-300' : 'text-slate-600'}">Expected Volatility: <span class="${isDark ? 'text-white' : 'text-slate-900'} font-bold">${params.data[0]}%</span></div>
            <div class="${isDark ? 'text-slate-300' : 'text-slate-600'}">Expected Return: <span class="${isDark ? 'text-white' : 'text-slate-900'} font-bold">${params.data[1]}%</span></div>
          </div>`;
        }
        return `<div class="font-mono text-xs">
          <div class="font-semibold text-fin-blue mb-1">${params.seriesName}</div>
          <div class="${isDark ? 'text-slate-300' : 'text-slate-600'}">Expected Volatility: <span class="${isDark ? 'text-white' : 'text-slate-900'} font-bold">${params.data[0]}%</span></div>
          <div class="${isDark ? 'text-slate-300' : 'text-slate-600'}">Expected Return: <span class="${isDark ? 'text-white' : 'text-slate-900'} font-bold">${params.data[1]}%</span></div>
        </div>`;
      },
    },
    legend: {
      top: 0,
      right: 0,
      textStyle: { color: mutedColor, fontSize: 11 },
    },
    grid: {
      left: '2%',
      right: '3%',
      bottom: '3%',
      top: '12%',
      containLabel: true,
    },
    xAxis: {
      type: 'value',
      name: 'Expected Annualized Volatility (Risk)',
      nameLocation: 'middle',
      nameGap: 30,
      nameTextStyle: { color: mutedColor, fontSize: 11 },
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: mutedColor, fontSize: 10, formatter: '{value}%' },
      splitLine: { lineStyle: { color: splitLineColor, type: 'dashed' } },
    },
    yAxis: {
      type: 'value',
      name: 'Expected Annual Return',
      nameLocation: 'middle',
      nameGap: 35,
      nameTextStyle: { color: mutedColor, fontSize: 11 },
      axisLine: { show: false },
      axisLabel: { color: '#64748B', fontSize: 10, formatter: '{value}%' },
      splitLine: { lineStyle: { color: '#162032', type: 'dashed' } },
    },
    series: [
      // 1. Efficient Frontier Curve
      {
        name: 'Efficient Frontier',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: curveData.length > 0 ? curveData : [
          [10, 8], [12, 12], [15, 16], [18, 19], [22, 22], [28, 25]
        ],
        lineStyle: { width: 3, color: '#3B82F6' },
      },
      // 2. Individual constituent assets
      {
        name: 'Assets',
        type: 'scatter',
        symbolSize: 10,
        data: assetScatterData,
        itemStyle: { color: '#94A3B8' },
        label: {
          show: true,
          formatter: (params: any) => params.data[2],
          position: 'top',
          color: '#94A3B8',
          fontSize: 10,
          fontFamily: 'monospace',
        },
      },
      // 3. Current Portfolio Point
      currentPoint && {
        name: 'Current Portfolio',
        type: 'scatter',
        symbolSize: 16,
        data: [[(currentPoint.volatility * 100).toFixed(2), (currentPoint.returnVal * 100).toFixed(2)]],
        itemStyle: { color: '#EC4899', borderColor: '#FFFFFF', borderWidth: 2 },
      },
      // 4. Max Sharpe Optimal Point
      maxSharpePoint && {
        name: 'Max Sharpe (Optimal)',
        type: 'scatter',
        symbolSize: 18,
        data: [[(maxSharpePoint.volatility * 100).toFixed(2), (maxSharpePoint.returnVal * 100).toFixed(2)]],
        itemStyle: { color: '#10B981', borderColor: '#FFFFFF', borderWidth: 2 },
      },
      // 5. Min Volatility Point
      minVolPoint && {
        name: 'Min Volatility',
        type: 'scatter',
        symbolSize: 16,
        data: [[(minVolPoint.volatility * 100).toFixed(2), (minVolPoint.returnVal * 100).toFixed(2)]],
        itemStyle: { color: '#F59E0B', borderColor: '#FFFFFF', borderWidth: 2 },
      },
    ].filter(Boolean),
  };

  return (
    <Card
      title="Markowitz Efficient Frontier"
      subtitle="Optimal risk-return trade-offs generated by Modern Portfolio Theory"
    >
      <div style={{ height }}>
        <ReactECharts option={option} style={{ height: '100%', width: '100%' }} notMerge={true} />
      </div>
    </Card>
  );
};
