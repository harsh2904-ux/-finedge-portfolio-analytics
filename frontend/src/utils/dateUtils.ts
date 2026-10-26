import { format, formatDistanceToNow, parseISO } from 'date-fns';

export function formatDate(dateStr?: string | null, formatStr: string = 'dd MMM yyyy'): string {
  if (!dateStr) return '—';
  try {
    const d = typeof dateStr === 'string' ? parseISO(dateStr) : new Date(dateStr);
    return format(d, formatStr);
  } catch {
    return dateStr;
  }
}

export function formatDateTime(dateStr?: string | null): string {
  return formatDate(dateStr, 'dd MMM yyyy, HH:mm:ss');
}

export function formatTimeAgo(dateStr?: string | null): string {
  if (!dateStr) return '—';
  try {
    const d = typeof dateStr === 'string' ? parseISO(dateStr) : new Date(dateStr);
    return formatDistanceToNow(d, { addSuffix: true });
  } catch {
    return 'recently';
  }
}
