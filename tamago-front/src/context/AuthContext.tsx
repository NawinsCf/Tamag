"use client";

import React, { createContext, useState, useEffect, ReactNode } from 'react';

type User = { id: number; pseudo: string; mail?: string; estAdmin?: boolean } | null;

type AuthContextType = {
  user: User;
  initialized: boolean;
  login: (user: User) => void;
  logout: () => void;
};

export const AuthContext = createContext<AuthContextType>({
  user: null,
  initialized: false,
  login: () => {},
  logout: () => {},
});

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User>(null);
  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    try {
      const raw = localStorage.getItem('tamago_user');
      if (raw) setUser(JSON.parse(raw));
    } catch {
      // ignore
    }
    // localStorage check is complete (even if no user found)
    setInitialized(true);
  }, []);

  useEffect(() => {
    if (user) localStorage.setItem('tamago_user', JSON.stringify(user));
    else localStorage.removeItem('tamago_user');
  }, [user]);

  const login = (u: User) => setUser(u);
  const logout = () => setUser(null);

  return (
    <AuthContext.Provider value={{ user, initialized, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
