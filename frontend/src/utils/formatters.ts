export function formatCurrency(
  value: number | null | undefined,
  currency: string = 'INR'
): string {
  if (value === null || value === undefined || isNaN(value)) {
    return '—';
  }

  const curr = currency.toUpperCase();
  const locale = curr === 'INR' ? 'en-IN' : curr === 'EUR' ? 'de-DE' : 'en-US';
  try {
    return new Intl.NumberFormat(locale, {
      style: 'currency',
      currency: curr === 'INR' ? 'INR' : curr === 'EUR' ? 'EUR' : 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  } catch {
    const sym = curr === 'INR' ? '₹' : curr === 'EUR' ? '€' : '$';
    return `${sym}${value.toFixed(2)}`;
  }
}

export function formatCompactCurrency(
  value: number | null | undefined,
  currency: string = 'INR'
): string {
  if (value === null || value === undefined || isNaN(value)) {
    return '—';
  }

  const curr = currency.toUpperCase();
  const sign = value < 0 ? '-' : '';
  const abs = Math.abs(value);
  const symbol = curr === 'INR' ? '₹' : curr === 'EUR' ? '€' : '$';

  if (curr === 'INR') {
    if (abs >= 10000000) return `${sign}${symbol}${(abs / 10000000).toFixed(2)}Cr`;
    if (abs >= 100000) return `${sign}${symbol}${(abs / 100000).toFixed(2)}L`;
    if (abs >= 1000) return `${sign}${symbol}${(abs / 1000).toFixed(1)}k`;
  } else {
    if (abs >= 1000000000) return `${sign}${symbol}${(abs / 1000000000).toFixed(2)}B`;
    if (abs >= 1000000) return `${sign}${symbol}${(abs / 1000000).toFixed(2)}M`;
    if (abs >= 1000) return `${sign}${symbol}${(abs / 1000).toFixed(1)}k`;
  }

  return `${sign}${symbol}${abs.toFixed(2)}`;
}

export function formatPercent(
  value: number | null | undefined,
  includeSign: boolean = true
): string {
  if (value === null || value === undefined || isNaN(value)) {
    return '—';
  }

  // Determine if value is decimal (0.12 = 12%) or already percent (12 = 12%)
  const pct = Math.abs(value) <= 1.0 && value !== 0 ? value * 100 : value;
  const formatted = pct.toFixed(2);
  const sign = includeSign && pct > 0 ? '+' : '';

  return `${sign}${formatted}%`;
}

export function formatNumber(
  value: number | null | undefined,
  decimals: number = 2
): string {
  if (value === null || value === undefined || isNaN(value)) {
    return '—';
  }

  return new Intl.NumberFormat('en-IN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  }).format(value);
}
