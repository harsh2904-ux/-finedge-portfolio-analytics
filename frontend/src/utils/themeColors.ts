import { useUiStore } from '../stores/uiStore';

export interface ChartThemeColors {
  isDark: boolean;
  textColor: string;
  mutedColor: string;
  axisLineColor: string;
  splitLineColor: string;
  tooltipBg: string;
  tooltipBorder: string;
  tooltipTextColor: string;
  cardBg: string;
  panelBg: string;
}

export const useChartTheme = (): ChartThemeColors => {
  const theme = useUiStore((state) => state.theme);
  const isDark = theme === 'dark';

  return {
    isDark,
    textColor: isDark ? '#F1F5F9' : '#0F172A',
    mutedColor: isDark ? '#8A97AB' : '#64748B',
    axisLineColor: isDark ? '#202B3E' : '#CBD5E1',
    splitLineColor: isDark ? '#1E293B' : '#E2E8F0',
    tooltipBg: isDark ? '#121824' : '#FFFFFF',
    tooltipBorder: isDark ? '#202B3E' : '#CBD5E1',
    tooltipTextColor: isDark ? '#F1F5F9' : '#0F172A',
    cardBg: isDark ? '#121824' : '#FFFFFF',
    panelBg: isDark ? '#1A2336' : '#F1F5F9',
  };
};
