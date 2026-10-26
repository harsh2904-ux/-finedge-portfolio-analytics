import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { KpiCard } from './KpiCard';

describe('KpiCard component', () => {
  it('renders title and value', () => {
    render(<KpiCard title="Portfolio Value" value="$50,000.00" />);
    expect(screen.getByText('Portfolio Value')).toBeInTheDocument();
    expect(screen.getByText('$50,000.00')).toBeInTheDocument();
  });

  it('renders positive change correctly', () => {
    render(<KpiCard title="Total P&L" value="$1,200.00" change={0.045} />);
    expect(screen.getByText('+4.50%')).toBeInTheDocument();
  });

  it('renders skeleton loading state', () => {
    const { container } = render(<KpiCard title="Beta" value="1.05" loading />);
    expect(container.querySelector('.animate-pulse')).toBeInTheDocument();
  });
});
