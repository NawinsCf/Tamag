"use client";

import React, { useState, useContext } from 'react';
import api from '../lib/api';
import { AuthContext } from '../src/context/AuthContext';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

export default function LoginForm({ showRegisterLink = false }: { showRegisterLink?: boolean }) {
  const [pseudo, setPseudo] = useState('');
  const [mdp, setMdp] = useState('');
  const [msg, setMsg] = useState<string | null>(null);
  const { login } = useContext(AuthContext);
  const router = useRouter();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMsg(null);
    try {
      const res = await api.post('/api/auth/login', { pseudo, mdp });
  login(res.data);
  // navigate client-side with Next router
  router.replace('/start');
    } catch (err) {
      const error = err as unknown;
      try {
        const e = error as { response?: { data?: unknown }; message?: string };
        if (typeof e.response?.data === 'string') setMsg(e.response.data);
        else if (typeof e.message === 'string') setMsg(e.message);
        else setMsg('Erreur');
      } catch {
        setMsg('Erreur');
      }
    }
  };

  return (
    <div className="px-4 sm:px-6 lg:px-8">
      <div className="flex items-center justify-center min-h-[60vh]">
        <main className="w-full max-w-md mx-auto p-6 bg-transparent">
        <h1 className="text-2xl font-bold mb-4">Se connecter</h1>
        <form onSubmit={submit} className="space-y-4">
          <input className="w-full p-2 border rounded" placeholder="pseudo" value={pseudo} onChange={e => setPseudo(e.target.value)} />
          <input type="password" className="w-full p-2 border rounded" placeholder="mdp" value={mdp} onChange={e => setMdp(e.target.value)} />
          <button type="submit" className="w-full px-4 py-2 bg-blue-600 text-white rounded">Se connecter</button>
        </form>
        {msg && <div className="mt-4 text-red-600">{msg}</div>}

        {showRegisterLink && (
          <div className="mt-6 text-center">
            <Link href="/register" className="inline-block px-4 py-2 bg-green-600 text-white rounded">Créer un compte</Link>
          </div>
        )}
        </main>
      </div>
    </div>
  );
}
