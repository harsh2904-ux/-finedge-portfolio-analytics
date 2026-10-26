import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from '../common/Card';
import { Skeleton } from '../common/Skeleton';
import { useChartTheme } from '../../utils/themeColors';

export interface AllocationItem {
  name: string;
  value: number; // weight percentage (e.g. 0.45 or 45) or dollar value
  formattedValue?: string;
  color?: string;
}

interface AllocationDonutChartProps {
  data: AllocationItem[];
  title?: string;
  subtitle?: string;
  isLoading?: boolean;
  height?: number;
  centerLabel?: string;
  centerValue?: string;
}

const PALETTE = [
  '#3B82F6', // Blue
  '#10B981', // Emerald
  '#F59E0B', // Amber
  '#8B5CF6', // Purple
  '#EC4899', // Pink
  '#06B6D4', // Cyan
  '#6366F1', // Indigo
  '#14B8A6', // Teal
  '#F97316', // Orange
];

export const AllocationDonutChart: React.FC<AllocationDonutChartProps> = ({
  data = [],
  title = 'Asset Allocation',
  subtitle = 'Portfolio breakdown by weight',
  isLoading = false,
  height = 320,
  centerLabel = 'Assets',
  centerValue,
}) => {
  const { isDark, cardBg, mutedColor, tooltipBg, tooltipBorder, tooltipTextColor } = useChartTheme();

  if (isLoading) {
    return (
      <Card title={title} subtitle={subtitle}>
        <Skeleton height={height} />
      </Card>
    );
  }

  // Format data for ECharts
  const seriesData = data.map((item, idx) => ({
    name: item.name,
    value: Number(item.value.toFixed(2)),
    itemStyle: { color: item.color || PALETTE[idx % PALETTE.length] },
  }));

  const totalPositions = data.length;
  const displayCenterValue = centerValue || `${totalPositions}`;

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipTextColor, fontSize: 12 },
      formatter: (params: any) => {
        return `<div class="font-mono text-xs">
          <div class="flex items-center gap-2 mb-1">
            <span class="w-2.5 h-2.5 rounded-full inline-block" style="background:${params.color}"></span>
            <span class="font-semibold ${isDark ? 'text-white' : 'text-slate-900'}">${params.name}</span>
          </div>
          <div class="flex justify-between gap-4 ${isDark ? 'text-slate-300' : 'text-slate-600'}">
            <span>Weight:</span>
            <span class="font-bold ${isDark ? 'text-white' : 'text-slate-900'}">${params.value}%</span>
          </div>
        </div>`;
      },
    },
    legend: {
      orient: 'vertical',
      right: '2%',
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 10,
      textStyle: { color: mutedColor, fontSize: 11 },
      formatter: (name: string) => {
        const item = seriesData.find((d) => d.name === name);
        return `${name} ${item ? `(${item.value}%)` : ''}`;
      },
    },
    series: [
      {
        name: 'Allocation',
        type: 'pie',
        radius: ['52%', '76%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: cardBg,
          borderWidth: 2,
        },
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold',
            color: '#FFFFFF',
            formatter: '{b}\n{c}%',
          },
        },
        labelLine: {
          show: false,
        },
        data: seriesData,
      },
    ],
  };

  return (
    <Card title={title} subtitle={subtitle}>
      <div className="relative" style={{ height }}>
        <ReactECharts option={option} style={{ height: '100%', width: '100%' }} notMerge={true} />
        {/* Center overlay if no emphasis */}
        <div className="pointer-events-none absolute left-[35%] top-[50%] -translate-x-1/2 -translate-y-1/2 text-center">
          <div className="text-xl font-bold font-mono text-fin-text">{displayCenterValue}</div>
          <div className="text-[10px] uppercase tracking-wider text-fin-muted font-medium">
            {centerLabel}
          </div>
        </div>
      </div>
    </Card>
  );
};
