import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';

/** Datos del usuario autenticado. */
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
  isLoading: boolean;
  login: (token: string, user: AuthUser) => void;
  logout: () => void;
  apiFetch: (input: RequestInfo | URL, init?: RequestInit) => Promise<Response>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Provee el estado de sesión a toda la aplicación, persistiendo el token
 * y el usuario en `localStorage` para sobrevivir a recargas de página.
 */
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

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
    setIsLoading(false);
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

  /**
   * Envoltorio de `fetch` que adjunta el token de sesión como header
   * `Authorization` cuando hay uno guardado y el llamador no definió el
   * suyo propio.
   */
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
      isLoading,
      login,
      logout,
      apiFetch,
    }),
    [user, isLoading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

/**
 * Hook de acceso al contexto de autenticación.
 *
 * @throws Error si se usa fuera de un `AuthProvider`
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
