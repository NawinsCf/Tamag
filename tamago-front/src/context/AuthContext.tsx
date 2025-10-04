"use client";

import React, { createContext, useState, useEffect, ReactNode } from 'react';
import * as auth from '../../lib/auth';
import '../../lib/axios-interceptor';

type User = { id: number; pseudo: string; mail?: string; estAdmin?: boolean } | null;

type AuthContextType = {
  user: User;
  initialized: boolean;
  login: (u: { pseudo: string; mdp: string }) => Promise<void>;
  logout: () => Promise<void>;
};

export const AuthContext = createContext<AuthContextType>({
  user: null,
  initialized: false,
  login: async () => {},
  logout: async () => {},
});

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User>(null);
  const [initialized, setInitialized] = useState(false);

  // On mount, check /me to populate user state
  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const res = await auth.me();
        if (!mounted) return;
        setUser(res.data);
      } catch (e) {
        // not authenticated
        if (!mounted) return;
        setUser(null);
      } finally {
        if (mounted) setInitialized(true);
      }
    })();
    return () => { mounted = false; };
  }, []);

  const login = async (credentials: { pseudo: string; mdp: string }) => {
    await auth.login(credentials.pseudo, credentials.mdp);
    try {
      const res = await auth.me();
      setUser(res.data);
    } catch {
      setUser(null);
    }
  };

  const logout = async () => {
    try {
      await auth.logout();
    } catch (e) {
      // ignore
    }
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, initialized, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
