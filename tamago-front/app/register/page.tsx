"use client";

import React, { useState, useContext } from 'react';
import api from '../../lib/api';
import { sha256Hex } from '../../lib/hash';
import { AuthContext } from '../../src/context/AuthContext';
import { useRouter } from 'next/navigation';

export default function RegisterPage() {
  const [pseudo, setPseudo] = useState('');
  const [mdp, setMdp] = useState('');
  const [mail, setMail] = useState('');
  const [msg, setMsg] = useState<string | null>(null);

  const { login } = useContext(AuthContext);
  const router = useRouter();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMsg(null);
    try {
      const hashed = await sha256Hex(mdp);
      const res = await api.post('/api/users', { pseudo, mdp: hashed, mail });
      // log the user in and redirect to start
      login(res.data);
      router.replace('/start');
    } catch (err) {
      const error = err as unknown;
      let message = 'Unknown error';
      try {
        const e = error as { response?: { data?: unknown }; message?: string };
        const respData = e.response?.data;
        if (typeof respData === 'string') message = respData;
        else if (typeof e.message === 'string') message = e.message;
      } catch {
        // ignore
      }
      setMsg(message);
    }
  };

  return (
    <main className="max-w-xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-4">Register</h1>
      <form onSubmit={submit} className="space-y-4">
        <input className="w-full p-2 border" placeholder="pseudo" value={pseudo} onChange={e => setPseudo(e.target.value)} />
        <input type="password" className="w-full p-2 border" placeholder="mdp" value={mdp} onChange={e => setMdp(e.target.value)} />
        <input className="w-full p-2 border" placeholder="mail" value={mail} onChange={e => setMail(e.target.value)} />
        <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded">Register</button>
      </form>
      {msg && <div className="mt-4">{msg}</div>}
    </main>
  );
}
