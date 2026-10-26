import { describe, it, expect } from 'vitest';
import { formatCurrency, formatPercent, formatNumber, formatCompactCurrency } from './formatters';

describe('formatters', () => {
  it('formats INR currency correctly by default', () => {
    const formatted = formatCurrency(12345.67);
    expect(formatted).toMatch(/12,345\.67|₹/);
  });

  it('formats compact INR with Lakhs and Crores', () => {
    expect(formatCompactCurrency(1500000, 'INR')).toBe('₹15.00L');
    expect(formatCompactCurrency(25000000, 'INR')).toBe('₹2.50Cr');
  });

  it('formats percentages correctly', () => {
    expect(formatPercent(0.1234)).toBe('+12.34%');
    expect(formatPercent(0.1234, false)).toBe('12.34%');
    expect(formatPercent(-0.052)).toBe('-5.20%');
  });

  it('formats numbers with commas', () => {
    expect(formatNumber(1000000, 0)).toBe('10,00,000');
    expect(formatNumber(1000000, 2)).toBe('10,00,000.00');
  });
});
