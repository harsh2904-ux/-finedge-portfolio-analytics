import React from 'react';
import { cn } from '../../utils/cn';

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'purple';
  size?: 'sm' | 'md';
  dot?: boolean;
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  variant = 'default',
  size = 'md',
  dot = false,
  className,
  ...props
}) => {
  const variantStyles = {
    default: 'bg-fin-panel text-fin-muted border-fin-border',
    success: 'bg-emerald-500/10 text-fin-emerald border-emerald-500/20',
    warning: 'bg-amber-500/10 text-fin-amber border-amber-500/20',
    danger: 'bg-red-500/10 text-fin-red border-red-500/20',
    info: 'bg-blue-500/10 text-fin-blue border-blue-500/20',
    purple: 'bg-purple-500/10 text-fin-purple border-purple-500/20',
  };

  const dotStyles = {
    default: 'bg-fin-muted',
    success: 'bg-fin-emerald',
    warning: 'bg-fin-amber',
    danger: 'bg-fin-red',
    info: 'bg-fin-blue',
    purple: 'bg-fin-purple',
  };

  const sizeStyles = {
    sm: 'text-[10px] px-2 py-0.5 font-medium',
    md: 'text-xs px-2.5 py-1 font-medium',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 rounded-md border font-sans select-none',
        variantStyles[variant],
        sizeStyles[size],
        className
      )}
      {...props}
    >
      {dot && <span className={cn('w-1.5 h-1.5 rounded-full shrink-0', dotStyles[variant])} />}
      <span>{children}</span>
    </span>
  );
};
