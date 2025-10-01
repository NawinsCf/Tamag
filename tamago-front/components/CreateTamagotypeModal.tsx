"use client";

import React, { useState } from 'react';
import api from '../lib/api';

type Props = { onClose: () => void; onCreated?: () => void };

export default function CreateTamagotypeModal({ onClose, onCreated }: Props) {
  const [nom, setNom] = useState('');
  const [descr, setDescr] = useState('');
  const [pv, setPv] = useState(10);
  const [pf, setPf] = useState(5);
  const [couleur, setCouleur] = useState('#ffcc00');
  const [valueFaim, setValueFaim] = useState(3);
  const [valueRegen, setValueRegen] = useState(1);
  const [estActif, setEstActif] = useState(true);
  // nomImg is now generated server-side. We only keep file state.
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const uploadImage = async (): Promise<string | null> => {
    if (!file) return null;
    const form = new FormData();
    form.append('file', file);
    try {
      const res = await fetch('/api/upload-image', { method: 'POST', body: form });
      if (!res.ok) throw new Error('Upload failed');
      const data = await res.json();
      return (data && data.filename) ? data.filename : null;
    } catch (e) {
      console.error(e);
      setError('Upload failed');
      return null;
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      let imageName = '';
      if (file) {
        const uploaded = await uploadImage();
        if (uploaded) imageName = uploaded;
        else throw new Error('upload');
      }

      const body = { nom, descr, pv, pf, couleur, valueFaim, valueRegen, estActif, nomImg: imageName };
      const res = await api.post('/api/tamagotype', body);
      if (res.status === 201 || res.status === 200) {
        if (onCreated) onCreated();
        onClose();
        } else {
          // If we uploaded an image but creation failed, remove the uploaded file
          if (imageName) {
            try {
              await fetch('/api/delete-image', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ filename: imageName }) });
            } catch (cleanupErr) {
              console.error('failed cleanup', cleanupErr);
            }
          }
          setError('Creation failed');
        }
    } catch (err) {
      const e = err as unknown as { message?: string };
      setError(e?.message || 'Error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center px-4 bg-black/40">
      <div className="bg-white rounded shadow-lg w-full max-w-2xl p-6 mx-auto">
        <div className="w-full max-w-xl mx-auto">
          <h2 className="text-lg font-semibold mb-4 text-center">Créer un Tamagotype</h2>
          {error && <div className="text-rose-600 mb-2">{error}</div>}
          <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="block text-sm">Nom *</label>
            <input className="w-full border p-2 rounded" value={nom} onChange={e => setNom(e.target.value)} required />
          </div>
          <div>
            <label className="block text-sm">Description *</label>
            <textarea className="w-full border p-2 rounded" value={descr} onChange={e => setDescr(e.target.value)} required />
          </div>
          <div className="flex gap-2">
            <div className="flex-1">
              <label className="block text-sm">PV</label>
              <input type="number" min={0} className="w-full border p-2 rounded" value={pv} onChange={e => setPv(Number(e.target.value))} />
            </div>
            <div className="flex-1">
              <label className="block text-sm">PF</label>
              <input type="number" min={0} className="w-full border p-2 rounded" value={pf} onChange={e => setPf(Number(e.target.value))} />
            </div>
            <div>
              <label className="block text-sm">Couleur</label>
              <input className="border p-2 rounded" value={couleur} onChange={e => setCouleur(e.target.value)} />
            </div>
          </div>

          <div className="flex gap-2">
            <div>
              <label className="block text-sm">Value Faim</label>
              <input type="number" className="border p-2 rounded" value={valueFaim} onChange={e => setValueFaim(Number(e.target.value))} />
            </div>
            <div>
              <label className="block text-sm">Value Regen</label>
              <input type="number" className="border p-2 rounded" value={valueRegen} onChange={e => setValueRegen(Number(e.target.value))} />
            </div>
            <div className="flex items-center">
              <label className="mr-2 text-sm">Actif</label>
              <input type="checkbox" checked={estActif} onChange={e => setEstActif(e.target.checked)} />
            </div>
          </div>

          <div>
            <label className="block text-sm">Upload image (optionnel)</label>
            <input
              type="file"
              accept="image/*"
              onChange={e => {
                const f = e.target.files ? e.target.files[0] : null;
                setFile(f);
              }}
            />
          </div>

          <div className="flex justify-end gap-2">
            <button type="button" onClick={onClose} className="px-4 py-2 rounded border">Annuler</button>
            <button type="submit" disabled={loading} className="px-4 py-2 rounded bg-emerald-600 text-white">{loading ? '...' : 'Créer'}</button>
          </div>
          </form>
        </div>
      </div>
    </div>
  );
}
