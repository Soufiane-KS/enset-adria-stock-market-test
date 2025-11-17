import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { login as loginRequest } from '../api';
import type { LoginResponse } from '../api';

type AuthContextValue = {
  token: string | null;
  username: string | null;
  expiresAt: number | null;
  loading: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const STORAGE_KEY = 'enset-stock-auth';

type StoredAuth = {
  token: string;
  username: string;
  expiresAt: number;
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [state, setState] = useState<StoredAuth | null>(() => {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as StoredAuth) : null;
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (state) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [state]);

  const login = async (username: string, password: string) => {
    setLoading(true);
    try {
      const response: LoginResponse = await loginRequest({ username, password });
      const payload: StoredAuth = {
        token: response.token,
        username,
        expiresAt: response.expiresAt,
      };
      setState(payload);
    } finally {
      setLoading(false);
    }
  };

  const logout = () => setState(null);

  const value = useMemo<AuthContextValue>(
    () => ({
      token: state?.token ?? null,
      username: state?.username ?? null,
      expiresAt: state?.expiresAt ?? null,
      loading,
      login,
      logout,
    }),
    [state, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

