"use client";

import React from 'react';

export default function Toast({ message, type = 'info', onClose }: { message: string; type?: 'info' | 'success' | 'error'; onClose?: () => void }) {
  if (!message) return null;
  const bg = type === 'success' ? 'bg-emerald-600' : type === 'error' ? 'bg-rose-600' : 'bg-sky-600';
  return (
    <div className={`fixed top-6 left-1/2 transform -translate-x-1/2 z-50 ${bg} text-white px-4 py-2 rounded shadow-md`} role="status">
      <div className="flex items-center gap-4">
        <div className="flex-1">{message}</div>
        <button onClick={onClose} aria-label="Close toast" className="opacity-90 hover:opacity-100">✕</button>
      </div>
    </div>
  );
}
