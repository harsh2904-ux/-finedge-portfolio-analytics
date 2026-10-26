import { create } from 'zustand';
import type { User, AuthResponse } from '../types/auth';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (data: AuthResponse) => void;
  setAuth: (token: string, user: User) => void;
  logout: () => void;
  setUser: (user: User) => void;
}

const getSavedUser = (): User | null => {
  try {
    const raw = localStorage.getItem('user_data');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};

export const useAuthStore = create<AuthState>((set) => ({
  user: getSavedUser(),
  token: localStorage.getItem('access_token'),
  isAuthenticated: !!localStorage.getItem('access_token'),

  login: (data: AuthResponse) => {
    const token = data.accessToken || data.token || '';
    localStorage.setItem('access_token', token);
    if (data.refreshToken) {
      localStorage.setItem('refresh_token', data.refreshToken);
    }
    localStorage.setItem('user_data', JSON.stringify(data.user));
    set({
      user: data.user,
      token,
      isAuthenticated: true,
    });
  },

  setAuth: (token: string, user: User) => {
    localStorage.setItem('access_token', token);
    localStorage.setItem('user_data', JSON.stringify(user));
    set({
      user,
      token,
      isAuthenticated: true,
    });
  },

  logout: () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user_data');
    set({
      user: null,
      token: null,
      isAuthenticated: false,
    });
  },

  setUser: (user: User) => {
    localStorage.setItem('user_data', JSON.stringify(user));
    set({ user });
  },
}));
