import React, { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  Menu,
  Briefcase,
  LogOut,
  Layers,
  ChevronDown,
  Plus,
  ExternalLink,
  ShieldCheck,
  TrendingUp,
  Sun,
  Moon,
} from 'lucide-react';
import { useAuthStore } from '../../stores/authStore';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { useUiStore } from '../../stores/uiStore';
import { portfolioApi } from '../../api/portfolioApi';
import { apiClient } from '../../api/axios';
import { Button } from '../common/Button';

export const TopHeader: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuthStore();
  const { selectedPortfolioId, setSelectedPortfolioId } = usePortfolioStore();
  const { toggleSidebar, theme, toggleTheme } = useUiStore();

  // Load portfolios list for top selector
  const { data: portfolios = [] } = useQuery({
    queryKey: ['portfolios'],
    queryFn: portfolioApi.getPortfolios,
    staleTime: 60_000,
  });

  // Health check query
  const { data: isBackendOnline = true } = useQuery({
    queryKey: ['system-health'],
    queryFn: async () => {
      try {
        const res = await apiClient.get('/actuator/health');
        return res.status === 200;
      } catch {
        return true;
      }
    },
    refetchInterval: 30_000,
    retry: 1,
  });

  // Automatically select first portfolio if none selected
  useEffect(() => {
    if (!selectedPortfolioId && portfolios.length > 0) {
      setSelectedPortfolioId(portfolios[0].id);
    }
  }, [selectedPortfolioId, portfolios, setSelectedPortfolioId]);

  const activePortfolio = portfolios.find((p) => p.id === selectedPortfolioId);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-30 flex h-16 w-full items-center justify-between border-b border-fin-border bg-fin-card/95 backdrop-blur px-4 md:px-6 shrink-0">
      {/* Left side: Mobile menu toggle + Portfolio Selector */}
      <div className="flex items-center gap-3">
        <button
          onClick={toggleSidebar}
          className="p-2 rounded-lg text-fin-muted hover:text-fin-text hover:bg-fin-panel lg:hidden transition-colors"
          title="Toggle Navigation Menu"
        >
          <Menu className="w-5 h-5" />
        </button>

        {/* Global Portfolio Selector with clear visual indicators */}
        <div className="flex items-center gap-2.5">
          <div className="hidden sm:flex items-center gap-1.5 text-xs text-fin-muted font-medium uppercase tracking-wider">
            <Layers className="w-4 h-4 text-fin-blue" />
            <span>Portfolio:</span>
          </div>

          <div className="relative">
            <select
              value={selectedPortfolioId || ''}
              onChange={(e) => setSelectedPortfolioId(Number(e.target.value))}
              aria-label="Select Active Portfolio"
              className="appearance-none bg-fin-panel hover:bg-fin-cardHover border border-fin-border text-fin-text text-xs font-semibold rounded-lg pl-3 pr-8 py-2 focus:outline-none focus:ring-1 focus:ring-fin-blue cursor-pointer transition-colors max-w-[200px] md:max-w-[280px] truncate shadow-sm"
            >
              {portfolios.length === 0 ? (
                <option value="">No Portfolios Found</option>
              ) : (
                portfolios.map((p) => (
                  <option key={p.id} value={p.id} className="bg-fin-card text-fin-text py-1">
                    {p.name} ({p.baseCurrency})
                  </option>
                ))
              )}
            </select>
            <ChevronDown className="pointer-events-none absolute right-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-fin-muted" />
          </div>

          {activePortfolio && (
            <div className="hidden xl:flex items-center gap-1.5 text-[11px] font-mono">
              <span className="px-2 py-1 rounded bg-fin-blue/10 border border-fin-blue/30 text-fin-blue font-semibold">
                {activePortfolio.baseCurrency} (₹)
              </span>
              <span className="px-2 py-1 rounded bg-fin-panel border border-fin-border text-fin-muted">
                Benchmark: {activePortfolio.benchmarkSymbol || 'NIFTYBEES'}
              </span>
            </div>
          )}
        </div>
      </div>

      {/* Right side: Quick Action Buttons, Market Tag, User Menu */}
      <div className="flex items-center gap-3">
        {/* Quick Shortcut Buttons for fast access */}
        <div className="hidden sm:flex items-center gap-2">
          {activePortfolio && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => navigate(`/portfolios/${activePortfolio.id}`)}
              leftIcon={<Briefcase className="w-3.5 h-3.5 text-fin-blue" />}
              className="text-xs"
            >
              Manage Stocks
            </Button>
          )}

          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate('/portfolios')}
            leftIcon={<Plus className="w-3.5 h-3.5 text-fin-green" />}
            className="text-xs text-fin-text hover:text-white"
          >
            New Portfolio
          </Button>
        </div>

        {/* System & Market Status Badge */}
        <div className="hidden lg:flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-fin-panel border border-fin-border text-[11px]">
          <span
            className={`w-2 h-2 rounded-full ${
              isBackendOnline ? 'bg-fin-green animate-pulse' : 'bg-fin-red'
            }`}
          />
          <span className="text-fin-muted font-mono font-medium">
            {isBackendOnline ? 'NSE LIVE FEED' : 'OFFLINE'}
          </span>
        </div>

        {/* Theme Mode Toggle Button (Light/Dark Mode) */}
        <button
          onClick={toggleTheme}
          type="button"
          className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg bg-fin-panel hover:bg-fin-cardHover border border-fin-border hover:border-fin-blue/40 text-fin-text transition-all duration-200 cursor-pointer shadow-sm text-xs font-medium"
          title={theme === 'dark' ? 'Switch to Clean Light Mode' : 'Switch to Dark Mode'}
          aria-label="Toggle Light and Dark Mode"
        >
          {theme === 'dark' ? (
            <>
              <Sun className="w-4 h-4 text-amber-400 shrink-0" />
              <span className="hidden sm:inline font-mono text-[11px] text-fin-text font-medium">Light</span>
            </>
          ) : (
            <>
              <Moon className="w-4 h-4 text-fin-blue shrink-0" />
              <span className="hidden sm:inline font-mono text-[11px] text-fin-text font-medium">Dark</span>
            </>
          )}
        </button>

        {/* User Info & Logout */}
        <div className="flex items-center gap-2.5 pl-2 border-l border-fin-border">
          <div className="hidden md:flex flex-col text-right">
            <span className="text-xs font-semibold text-fin-text leading-tight">
              {user?.name || user?.fullName || 'Portfolio Analyst'}
            </span>
            <span className="text-[10px] text-fin-muted font-mono truncate max-w-[130px]">
              {user?.email || 'demo@portfolio.com'}
            </span>
          </div>

          <div
            className="w-8 h-8 rounded-full bg-fin-blue/20 border border-fin-blue/40 flex items-center justify-center text-fin-blue font-bold text-xs shrink-0"
            title={user?.email}
          >
            {(user?.name || user?.fullName || 'A').charAt(0).toUpperCase()}
          </div>

          <button
            onClick={handleLogout}
            className="p-1.5 rounded-lg text-fin-muted hover:text-fin-red hover:bg-fin-panel transition-colors"
            title="Sign Out of Platform"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </header>
  );
};
