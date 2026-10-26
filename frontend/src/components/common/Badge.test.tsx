import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Badge } from './Badge';

describe('Badge component', () => {
  it('renders badge label text', () => {
    render(<Badge variant="success">EQUITY</Badge>);
    expect(screen.getByText('EQUITY')).toBeInTheDocument();
  });

  it('renders with dot indicator when dot prop is true', () => {
    const { container } = render(<Badge variant="warning" dot>PENDING</Badge>);
    const dot = container.querySelector('.rounded-full');
    expect(dot).toBeInTheDocument();
  });
});
