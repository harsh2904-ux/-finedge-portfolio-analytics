import { create } from 'zustand';

interface PortfolioState {
  activePortfolioId: number | null;
  setActivePortfolioId: (id: number | null) => void;
  // Aliases for convenience
  selectedPortfolioId: number | null;
  setSelectedPortfolioId: (id: number | null) => void;
}

const getInitialPortfolioId = (): number | null => {
  const saved = localStorage.getItem('active_portfolio_id');
  return saved ? parseInt(saved, 10) : null;
};

export const usePortfolioStore = create<PortfolioState>((set) => {
  const initialId = getInitialPortfolioId();
  return {
    activePortfolioId: initialId,
    selectedPortfolioId: initialId,
    setActivePortfolioId: (id: number | null) => {
      if (id !== null) {
        localStorage.setItem('active_portfolio_id', id.toString());
      } else {
        localStorage.removeItem('active_portfolio_id');
      }
      set({ activePortfolioId: id, selectedPortfolioId: id });
    },
    setSelectedPortfolioId: (id: number | null) => {
      if (id !== null) {
        localStorage.setItem('active_portfolio_id', id.toString());
      } else {
        localStorage.removeItem('active_portfolio_id');
      }
      set({ activePortfolioId: id, selectedPortfolioId: id });
    },
  };
});
