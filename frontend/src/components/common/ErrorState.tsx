import React from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';
import { Button } from './Button';
import { cn } from '../../utils/cn';

interface ErrorStateProps {
  title?: string;
  message?: string;
  onRetry?: () => void;
  className?: string;
}

export const ErrorState: React.FC<ErrorStateProps> = ({
  title = 'Failed to load analytics',
  message = 'There was a problem fetching the financial data. Please check your connection and retry.',
  onRetry,
  className,
}) => {
  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center p-8 text-center rounded-xl border border-red-500/20 bg-red-500/5',
        className
      )}
    >
      <div className="p-3 rounded-full bg-red-500/10 border border-red-500/20 text-fin-red mb-3">
        <AlertTriangle className="w-6 h-6" />
      </div>
      <h4 className="text-sm font-semibold text-fin-text">{title}</h4>
      <p className="text-xs text-fin-muted mt-1 max-w-sm">{message}</p>
      {onRetry && (
        <div className="mt-4">
          <Button variant="secondary" size="sm" onClick={onRetry} leftIcon={<RefreshCw className="w-3.5 h-3.5" />}>
            Retry Calculation
          </Button>
        </div>
      )}
    </div>
  );
};
