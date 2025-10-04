"use client";
import React, { useContext } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { AuthContext } from '../src/context/AuthContext';

export default function NavBar() {
  const { user, logout } = useContext(AuthContext);
  const router = useRouter();

  return (
    <nav className="w-full bg-white shadow p-4 flex justify-between items-center">
      <Link href="/" className="font-bold">Tamago</Link>

      <div className="flex items-center gap-2">
        {user ? (
          <>
            {user.estAdmin && (
              <span className="px-2 py-1 text-sm font-semibold text-red-600 ">mode Admin</span>
            )}
          </>
        ) : null}
      </div>

      <div className="flex items-right gap-2">
        {user ? (
          <>
 
            <button onClick={async () => { await logout(); router.replace('/'); }} className="px-3 py-1 border rounded">Se déconnecter</button>
          </>
        ) : (
          <Link href="/" className="px-3 py-1 border rounded">Se connecter</Link>
        )}
      </div>
    </nav>
  );
}
