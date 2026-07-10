import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';

export interface AuthUser {
  id: number;
  email: string;
  nombre: string;
  rol: string;
}

interface AuthContextType {
  user: AuthUser | null;
  role: string | null;
  isAuthenticated: boolean;
  login: (token: string, user: AuthUser) => void;
  logout: () => void;
  apiFetch: (input: RequestInfo | URL, init?: RequestInit) => Promise<Response>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthUser | null>(null);

  useEffect(() => {
    const storedToken = window.localStorage.getItem('auth_token');
    const storedUser = window.localStorage.getItem('auth_user');
    if (storedToken && storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch {
        window.localStorage.removeItem('auth_token');
        window.localStorage.removeItem('auth_user');
      }
    }
  }, []);

  const login = (token: string, nextUser: AuthUser) => {
    window.localStorage.setItem('auth_token', token);
    window.localStorage.setItem('auth_user', JSON.stringify(nextUser));
    setUser(nextUser);
  };

  const logout = () => {
    window.localStorage.removeItem('auth_token');
    window.localStorage.removeItem('auth_user');
    setUser(null);
  };

  const apiFetch = async (input: RequestInfo | URL, init?: RequestInit) => {
    const token = window.localStorage.getItem('auth_token');
    const headers = new Headers(init?.headers || {});
    if (token && !headers.has('Authorization')) {
      headers.set('Authorization', `Bearer ${token}`);
    }
    return fetch(input, { ...init, headers });
  };

  const value = useMemo(
    () => ({
      user,
      role: user?.rol ?? null,
      isAuthenticated: Boolean(user),
      login,
      logout,
      apiFetch,
    }),
    [user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
