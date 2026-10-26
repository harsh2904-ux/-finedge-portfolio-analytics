import React from 'react';
import { cn } from '../../utils/cn';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  title?: string;
  subtitle?: string;
  action?: React.ReactNode;
  headerClassName?: string;
  noPadding?: boolean;
}

export const Card: React.FC<CardProps> = ({
  children,
  title,
  subtitle,
  action,
  className,
  headerClassName,
  noPadding = false,
  ...props
}) => {
  return (
    <div
      className={cn(
        'bg-fin-card border border-fin-border rounded-xl shadow-card transition-all duration-200',
        className
      )}
      {...props}
    >
      {(title || subtitle || action) && (
        <div
          className={cn(
            'px-5 py-4 border-b border-fin-border flex items-center justify-between gap-4',
            headerClassName
          )}
        >
          <div>
            {title && <h3 className="text-sm font-semibold text-fin-text tracking-wide">{title}</h3>}
            {subtitle && <p className="text-xs text-fin-muted mt-0.5">{subtitle}</p>}
          </div>
          {action && <div className="flex items-center gap-2">{action}</div>}
        </div>
      )}
      <div className={cn(!noPadding && 'p-5')}>{children}</div>
    </div>
  );
};
