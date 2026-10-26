import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from '../common/Card';
import { Skeleton } from '../common/Skeleton';
import { useChartTheme } from '../../utils/themeColors';

interface RiskItem {
  symbol: string;
  weight: number; // e.g. 0.40 for 40%
  riskContribution: number; // e.g. 0.55 for 55%
}

interface RiskContributionBarChartProps {
  data: RiskItem[];
  isLoading?: boolean;
  height?: number;
}

export const RiskContributionBarChart: React.FC<RiskContributionBarChartProps> = ({
  data = [],
  isLoading = false,
  height = 320,
}) => {
  const { isDark, mutedColor, textColor, axisLineColor, splitLineColor, tooltipBg, tooltipBorder, tooltipTextColor } = useChartTheme();

  if (isLoading) {
    return (
      <Card
        title="Risk Contribution vs Weight"
        subtitle="Identifies concentrated risk drivers"
      >
        <Skeleton height={height} />
      </Card>
    );
  }

  const symbols = data.map((d) => d.symbol);
  const weights = data.map((d) => (d.weight * 100).toFixed(1));
  const riskContributions = data.map((d) => (d.riskContribution * 100).toFixed(1));

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
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
      data: ['Asset Weight', 'Risk Contribution'],
      top: 0,
      right: 0,
      textStyle: { color: mutedColor, fontSize: 11 },
      icon: 'roundRect',
    },
    grid: {
      left: '2%',
      right: '2%',
      bottom: '3%',
      top: '14%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: symbols.length > 0 ? symbols : ['RELIANCE', 'TCS', 'HDFCBANK', 'INFY'],
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: textColor, fontSize: 11, fontWeight: 'bold' },
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
        name: 'Asset Weight',
        type: 'bar',
        barWidth: '22%',
        data: weights.length > 0 ? weights : ['40.0', '30.0', '20.0', '10.0'],
        itemStyle: {
          color: '#3B82F6',
          borderRadius: [4, 4, 0, 0],
        },
      },
      {
        name: 'Risk Contribution',
        type: 'bar',
        barWidth: '22%',
        data: riskContributions.length > 0 ? riskContributions : ['52.0', '28.0', '15.0', '5.0'],
        itemStyle: {
          color: '#F59E0B',
          borderRadius: [4, 4, 0, 0],
        },
      },
    ],
  };

  return (
    <Card
      title="Risk Contribution vs Capital Allocation"
      subtitle="Exposes assets contributing disproportionately more volatility than their capital weight"
    >
      <div style={{ height }}>
        <ReactECharts option={option} style={{ height: '100%', width: '100%' }} notMerge={true} />
      </div>
    </Card>
  );
};
