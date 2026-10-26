import { describe, it, expect, beforeEach } from 'vitest';
import { useAuthStore } from './authStore';

describe('authStore', () => {
  beforeEach(() => {
    localStorage.clear();
    useAuthStore.getState().logout();
  });

  it('initializes with null user and unauthenticated', () => {
    const state = useAuthStore.getState();
    expect(state.user).toBeNull();
    expect(state.isAuthenticated).toBe(false);
  });

  it('updates state upon login', () => {
    useAuthStore.getState().login({
      accessToken: 'mock_jwt_token',
      refreshToken: 'mock_refresh_token',
      tokenType: 'Bearer',
      expiresInMs: 3600000,
      user: {
        id: 1,
        name: 'Lead Analyst',
        email: 'analyst@fund.com',
        role: 'USER',
        active: true,
      },
    });

    const state = useAuthStore.getState();
    expect(state.isAuthenticated).toBe(true);
    expect(state.token).toBe('mock_jwt_token');
    expect(state.user?.name).toBe('Lead Analyst');
  });

  it('clears state upon logout', () => {
    useAuthStore.getState().setAuth('mock_token', {
      id: 1,
      name: 'Analyst',
      email: 'analyst@fund.com',
      role: 'USER',
      active: true,
    });

    useAuthStore.getState().logout();

    const state = useAuthStore.getState();
    expect(state.isAuthenticated).toBe(false);
    expect(state.token).toBeNull();
    expect(state.user).toBeNull();
  });
});
