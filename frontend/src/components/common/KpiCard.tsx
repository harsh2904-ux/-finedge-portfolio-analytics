import React from 'react';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { cn } from '../../utils/cn';

interface KpiCardProps {
  title: string;
  value: string | number;
  change?: number; // e.g., 0.024 for +2.4%
  changeLabel?: string;
  subtext?: string;
  icon?: React.ReactNode;
  trend?: 'up' | 'down' | 'neutral';
  isInverse?: boolean; // if true, up is bad (red), down is good (green), e.g. for VaR or Drawdown
  loading?: boolean;
  className?: string;
}

export const KpiCard: React.FC<KpiCardProps> = ({
  title,
  value,
  change,
  changeLabel,
  subtext,
  icon,
  trend,
  isInverse = false,
  loading = false,
  className,
}) => {
  if (loading) {
    return (
      <div className={cn('bg-fin-card border border-fin-border rounded-xl p-5 animate-pulse', className)}>
        <div className="flex justify-between items-center mb-3">
          <div className="h-3 bg-fin-border rounded w-24"></div>
          <div className="h-6 w-6 bg-fin-border rounded-lg"></div>
        </div>
        <div className="h-7 bg-fin-border rounded w-32 mb-2"></div>
        <div className="h-3 bg-fin-border rounded w-20"></div>
      </div>
    );
  }

  // Determine trend direction and colors
  let effectiveTrend: 'up' | 'down' | 'neutral' = trend || 'neutral';
  if (change !== undefined && trend === undefined) {
    if (change > 0) effectiveTrend = 'up';
    else if (change < 0) effectiveTrend = 'down';
    else effectiveTrend = 'neutral';
  }

  const isPositiveGood = !isInverse;
  const isGood =
    (effectiveTrend === 'up' && isPositiveGood) || (effectiveTrend === 'down' && !isPositiveGood);
  const isBad =
    (effectiveTrend === 'down' && isPositiveGood) || (effectiveTrend === 'up' && !isInverse ? false : true);

  const deltaColor =
    effectiveTrend === 'neutral'
      ? 'text-fin-muted'
      : isGood
      ? 'text-fin-emerald'
      : 'text-fin-red';

  const DeltaIcon =
    effectiveTrend === 'up'
      ? TrendingUp
      : effectiveTrend === 'down'
      ? TrendingDown
      : Minus;

  return (
    <div
      className={cn(
        'bg-fin-card border border-fin-border hover:border-fin-borderLight rounded-xl p-5 shadow-card transition-all duration-200',
        className
      )}
    >
      <div className="flex items-center justify-between gap-2 mb-2">
        <span className="text-xs font-medium uppercase tracking-wider text-fin-muted truncate">
          {title}
        </span>
        {icon && (
          <div className="p-2 rounded-lg bg-fin-panel border border-fin-border text-fin-muted shrink-0">
            {icon}
          </div>
        )}
      </div>

      <div className="flex items-baseline gap-2">
        <span className="text-2xl font-bold font-mono tracking-tight text-fin-text">{value}</span>
      </div>

      <div className="mt-2.5 flex items-center justify-between text-xs">
        {change !== undefined && (
          <div className={cn('inline-flex items-center gap-1 font-mono font-medium', deltaColor)}>
            <DeltaIcon className="w-3.5 h-3.5" />
            <span>
              {change > 0 ? '+' : ''}
              {(change * 100).toFixed(2)}%
            </span>
            {changeLabel && <span className="text-fin-muted font-sans font-normal ml-0.5">{changeLabel}</span>}
          </div>
        )}
        {subtext && <span className="text-fin-muted text-[11px] truncate">{subtext}</span>}
      </div>
    </div>
  );
};
