export type UserRole = 'USER' | 'ADMIN';

export interface User {
  id: number;
  name: string;
  fullName?: string;
  email: string;
  role: UserRole;
  active: boolean;
  createdAt?: string;
}

export interface AuthResponse {
  accessToken: string;
  token?: string;
  refreshToken: string;
  tokenType: string;
  expiresInMs: number;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  fullName?: string;
  email: string;
  password: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}
