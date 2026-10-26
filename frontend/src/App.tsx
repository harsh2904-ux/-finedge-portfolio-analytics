import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'sonner';

// Layout & Guards
import { AppLayout } from './components/layout/AppLayout';
import { ProtectedRoute } from './components/layout/ProtectedRoute';

// Pages
import { Login } from './pages/auth/Login';
import { Register } from './pages/auth/Register';
import { DashboardPage } from './pages/dashboard/DashboardPage';
import { PortfoliosPage } from './pages/portfolios/PortfoliosPage';
import { PortfolioDetailPage } from './pages/portfolios/PortfolioDetailPage';
import { MarketsPage } from './pages/markets/MarketsPage';
import { PerformancePage } from './pages/analytics/PerformancePage';
import { RiskPage } from './pages/analytics/RiskPage';
import { DiversificationPage } from './pages/analytics/DiversificationPage';
import { OptimizationPage } from './pages/quant/OptimizationPage';
import { SimulationPage } from './pages/quant/SimulationPage';
import { ScenariosPage } from './pages/quant/ScenariosPage';
import { BiReportsPage } from './pages/reports/BiReportsPage';
import { SettingsPage } from './pages/settings/SettingsPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      staleTime: 30_000,
      retry: 1,
    },
  },
});

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          {/* Public Authentication Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected Application Routes */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="portfolios" element={<PortfoliosPage />} />
            <Route path="portfolios/:id" element={<PortfolioDetailPage />} />
            <Route path="markets" element={<MarketsPage />} />
            <Route path="performance" element={<PerformancePage />} />
            <Route path="risk" element={<RiskPage />} />
            <Route path="diversification" element={<DiversificationPage />} />
            <Route path="optimization" element={<OptimizationPage />} />
            <Route path="simulation" element={<SimulationPage />} />
            <Route path="scenarios" element={<ScenariosPage />} />
            <Route path="reports" element={<BiReportsPage />} />
            <Route path="settings" element={<SettingsPage />} />
          </Route>

          {/* Wildcard Fallback */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
      <Toaster position="top-right" richColors theme="dark" />
    </QueryClientProvider>
  );
};

export default App;
