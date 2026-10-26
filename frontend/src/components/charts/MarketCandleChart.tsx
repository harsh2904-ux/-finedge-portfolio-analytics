import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from '../common/Card';
import { Skeleton } from '../common/Skeleton';
import { useChartTheme } from '../../utils/themeColors';
import type { PriceBar } from '../../types/market';

interface MarketCandleChartProps {
  symbol: string;
  data: PriceBar[];
  isLoading?: boolean;
  height?: number;
}

export const MarketCandleChart: React.FC<MarketCandleChartProps> = ({
  symbol,
  data = [],
  isLoading = false,
  height = 380,
}) => {
  const { mutedColor, axisLineColor, splitLineColor, tooltipBg, tooltipBorder, tooltipTextColor } = useChartTheme();

  if (isLoading) {
    return (
      <Card title={`${symbol} Price Chart`} subtitle="Daily OHLC candlesticks & trading volume">
        <Skeleton height={height} />
      </Card>
    );
  }

  // Sort chronological
  const sorted = [...data].sort((a, b) =>
    (a.date || a.timestamp || '').localeCompare(b.date || b.timestamp || '')
  );
  const dates = sorted.map((d) => (d.date || d.timestamp || '').split('T')[0]);
  // ECharts candlestick format: [open, close, lowest, highest]
  const candleValues = sorted.map((d) => [d.open, d.close, d.low, d.high]);
  const volumes = sorted.map((d, idx) => [
    idx,
    d.volume,
    d.close >= d.open ? 1 : -1,
  ]);

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipTextColor, fontSize: 12 },
    },
    grid: [
      { left: '3%', right: '2%', height: '60%', top: '10%' },
      { left: '3%', right: '2%', top: '75%', height: '18%' },
    ],
    xAxis: [
      {
        type: 'category',
        data: dates,
        axisLine: { lineStyle: { color: axisLineColor } },
        axisLabel: { color: mutedColor, fontSize: 10 },
      },
      {
        type: 'category',
        gridIndex: 1,
        data: dates,
        axisLine: { lineStyle: { color: axisLineColor } },
        axisLabel: { show: false },
      },
    ],
    yAxis: [
      {
        scale: true,
        axisLine: { show: false },
        axisLabel: { color: mutedColor, fontSize: 10, formatter: '₹{value}' },
        splitLine: { lineStyle: { color: splitLineColor, type: 'dashed' } },
      },
      {
        scale: true,
        gridIndex: 1,
        splitNumber: 2,
        axisLine: { show: false },
        axisLabel: { color: mutedColor, fontSize: 9 },
        splitLine: { show: false },
      },
    ],
    series: [
      {
        name: symbol,
        type: 'candlestick',
        data: candleValues,
        itemStyle: {
          color: '#10B981', // green bull
          color0: '#EF4444', // red bear
          borderColor: '#10B981',
          borderColor0: '#EF4444',
        },
      },
      {
        name: 'Volume',
        type: 'bar',
        xAxisIndex: 1,
        yAxisIndex: 1,
        data: volumes.map((v) => v[1]),
        itemStyle: {
          color: (params: any) => {
            const isBull = volumes[params.dataIndex]?.[2] === 1;
            return isBull ? 'rgba(16, 185, 129, 0.4)' : 'rgba(239, 68, 68, 0.4)';
          },
        },
      },
    ],
  };

  return (
    <Card
      title={`${symbol} Candlestick Analysis`}
      subtitle="Historical price trajectory (OHLC) & volume bars"
    >
      <div style={{ height }}>
        {data.length > 0 ? (
          <ReactECharts option={option} style={{ height: '100%', width: '100%' }} notMerge={true} />
        ) : (
          <div className="flex items-center justify-center h-full text-fin-muted text-xs">
            No historical price bars available for {symbol}
          </div>
        )}
      </div>
    </Card>
  );
};
