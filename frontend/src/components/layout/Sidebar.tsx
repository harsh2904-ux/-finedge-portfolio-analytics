import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Briefcase,
  TrendingUp,
  LineChart,
  ShieldAlert,
  PieChart,
  Sliders,
  Dice5,
  Flame,
  FileBarChart,
  Settings,
  ChevronLeft,
  X,
  Zap,
} from 'lucide-react';
import { useUiStore } from '../../stores/uiStore';
import { cn } from '../../utils/cn';

interface NavItem {
  name: string;
  path: string;
  icon: React.ReactNode;
  badge?: string;
}

interface NavSection {
  title: string;
  items: NavItem[];
}

export const Sidebar: React.FC = () => {
  const { sidebarOpen, setSidebarOpen, sidebarCollapsed, setSidebarCollapsed } = useUiStore();
  const location = useLocation();

  const sections: NavSection[] = [
    {
      title: 'MY STOCKS & OVERVIEW',
      items: [
        { name: 'Dashboard Overview', path: '/dashboard', icon: <LayoutDashboard className="w-4 h-4" /> },
        { name: 'My Stocks & Trades', path: '/portfolios', icon: <Briefcase className="w-4 h-4" /> },
        { name: 'Live Market Watch', path: '/markets', icon: <TrendingUp className="w-4 h-4" /> },
      ],
    },
    {
      title: 'PROFIT & SAFETY CHECK',
      items: [
        { name: 'Profit vs Nifty 50', path: '/performance', icon: <LineChart className="w-4 h-4" />, badge: 'Beat Market?' },
        { name: 'Risk & Loss Limits', path: '/risk', icon: <ShieldAlert className="w-4 h-4" />, badge: 'Safety' },
        { name: 'Diversification Check', path: '/diversification', icon: <PieChart className="w-4 h-4" /> },
      ],
    },
    {
      title: 'SMART AI TOOLS',
      items: [
        { name: 'Smart Stock Balancer', path: '/optimization', icon: <Sliders className="w-4 h-4" />, badge: 'AI' },
        { name: 'Future Money Predictor', path: '/simulation', icon: <Dice5 className="w-4 h-4" /> },
        { name: 'Market Crash Test', path: '/scenarios', icon: <Flame className="w-4 h-4" /> },
      ],
    },
    {
      title: 'REPORTS & EXPORT',
      items: [
        { name: '1-Click PDF Report', path: '/reports', icon: <FileBarChart className="w-4 h-4" />, badge: 'Print' },
        { name: 'Settings', path: '/settings', icon: <Settings className="w-4 h-4" /> },
      ],
    },
  ];

  return (
    <>
      {/* Mobile backdrop */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/70 backdrop-blur-sm lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar Container */}
      <aside
        className={cn(
          'fixed top-0 bottom-0 left-0 z-50 flex flex-col border-r border-fin-border bg-fin-card transition-all duration-300 lg:static',
          sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0',
          sidebarCollapsed ? 'w-20' : 'w-64'
        )}
      >
        {/* Brand Header */}
        <div className="flex h-16 items-center justify-between px-5 border-b border-fin-border shrink-0">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-fin-blue text-white shadow-md shadow-blue-500/20 shrink-0">
              <Zap className="w-4 h-4" />
            </div>
            {!sidebarCollapsed && (
              <div className="flex flex-col truncate">
                <span className="font-bold text-sm tracking-tight text-fin-text leading-tight">
                  FINEDGE
                </span>
                <span className="text-[10px] font-mono tracking-wider text-fin-blue font-semibold uppercase">
                  Portfolio Analytics
                </span>
              </div>
            )}
          </div>

          {/* Close for mobile */}
          <button
            onClick={() => setSidebarOpen(false)}
            className="p-1 rounded text-fin-muted hover:text-fin-text lg:hidden"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation Items */}
        <div className="flex-1 overflow-y-auto px-3 py-4 space-y-6 scrollbar-thin">
          {sections.map((section, idx) => (
            <div key={idx} className="space-y-1">
              {!sidebarCollapsed && (
                <div className="px-3 text-[10px] font-bold uppercase tracking-wider text-fin-muted mb-2 font-mono">
                  {section.title}
                </div>
              )}
              {section.items.map((item) => {
                const isActive = location.pathname === item.path || (item.path !== '/dashboard' && location.pathname.startsWith(item.path));
                return (
                  <NavLink
                    key={item.path}
                    to={item.path}
                    onClick={() => setSidebarOpen(false)}
                    className={cn(
                      'group flex items-center gap-3 px-3 py-2 text-xs font-medium rounded-lg transition-colors',
                      isActive
                        ? 'bg-fin-blue/15 text-fin-blue font-semibold shadow-sm'
                        : 'text-fin-muted hover:bg-fin-panel hover:text-fin-text'
                    )}
                    title={sidebarCollapsed ? item.name : undefined}
                  >
                    <span className={cn('shrink-0', isActive ? 'text-fin-blue' : 'text-fin-muted group-hover:text-fin-text')}>
                      {item.icon}
                    </span>
                    {!sidebarCollapsed && (
                      <span className="flex-1 truncate">{item.name}</span>
                    )}
                    {!sidebarCollapsed && item.badge && (
                      <span className="text-[9px] px-1.5 py-0.5 rounded bg-fin-panel border border-fin-border text-fin-muted font-mono font-medium">
                        {item.badge}
                      </span>
                    )}
                  </NavLink>
                );
              })}
            </div>
          ))}
        </div>

        {/* Footer Collapse Toggle */}
        <div className="p-3 border-t border-fin-border hidden lg:block shrink-0">
          <button
            onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
            className="w-full flex items-center justify-center gap-2 p-2 rounded-lg text-fin-muted hover:text-fin-text hover:bg-fin-panel text-xs transition-colors"
            title={sidebarCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            <ChevronLeft
              className={cn(
                'w-4 h-4 transition-transform duration-200',
                sidebarCollapsed && 'rotate-180'
              )}
            />
            {!sidebarCollapsed && <span>Collapse Sidebar</span>}
          </button>
        </div>
      </aside>
    </>
  );
};
