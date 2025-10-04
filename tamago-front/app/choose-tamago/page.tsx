"use client";

import React, { useEffect, useState, useContext } from 'react';
import dynamic from 'next/dynamic';
import api, { feedApi } from '../../lib/api';
import { AuthContext } from '../../src/context/AuthContext';
import { useRouter } from 'next/navigation';

const Carousel = dynamic(() => import('../../components/Carousel'), { ssr: false });
const Ctamag = dynamic(() => import('../../components/Ctamag'), { ssr: false });

export default function ChooseTamagoPage() {
  const { user } = useContext(AuthContext);
  const router = useRouter();

  type TamagoType = {
    id?: number | string;
    nom?: string;
    name?: string;
    nomImg?: string | null;
    nomimg?: string | null;
    image?: string | null;
    pv?: number | null;
    PV?: number | null;
    pf?: number | null;
    PF?: number | null;
  };

  const [types, setTypes] = useState<TamagoType[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const res = await api.get('/api/tamagotype/active');
        if (!mounted) return;
        // ensure array
        const data: TamagoType[] = Array.isArray(res.data) ? (res.data as TamagoType[]) : [];
        setTypes(data);
      } catch (err) {
        console.error('Failed to load tamagotypes', err);
        if (mounted) setError('Impossible de charger les modèles');
      } finally {
        if (mounted) setLoading(false);
      }
    })();
    return () => { mounted = false; };
  }, []);

  if (loading) return <main className="p-6"> <h1 className="text-xl">Chargement...</h1> </main>;
  if (error) return <main className="p-6"> <h1 className="text-xl text-red-600">{error}</h1> </main>;

  return (
    <main className="p-6">
      <h1 className="text-2xl font-bold mb-4">Choisir un Tamago</h1>
      {(!types || types.length === 0) ? (
        <div>Aucun modèle actif disponible.</div>
      ) : (
        <div className="max-w-5xl mx-auto">
          <Carousel loop>
            {types.map((t: TamagoType, idx: number) => (
              <div
                key={t.id}
                onClick={() => setSelectedIndex(idx)}
                role="button"
                tabIndex={0}
                onKeyDown={(e) => { if (e.key === 'Enter' || e.key === ' ') setSelectedIndex(idx); }}
                className={`cursor-pointer rounded-lg p-1 transition-shadow duration-200 ${selectedIndex === idx ? 'ring-4 ring-orange-400 shadow-lg' : 'hover:shadow-md'}`}
              >
                <Ctamag
                  label={t.nom || t.name || `#${t.id}`}
                  imageSrc={(t.nomImg ?? t.nomimg ?? t.image) ?? undefined}
                  imageAlt={t.nom || t.name || `Tamago ${t.id}`}
                  tamagoType={{ PV: t.PV ?? t.pv ?? null, PF: t.PF ?? t.pf ?? null, nomImg: t.nomImg ?? t.nomimg ?? t.image ?? null, image: t.image ?? null }}
                />
              </div>
            ))}
          </Carousel>

          <div className="flex items-center justify-center gap-4 mt-6">
            <button
              onClick={() => router.push('/start')}
              className="px-6 py-2 rounded bg-gray-200 hover:bg-gray-300 text-gray-800"
              disabled={busy}
            >
              Annuler
            </button>

            <button
              onClick={async () => {
                if (!user) {
                  alert('Vous devez être connecté pour choisir un tamago.');
                  router.push('/start');
                  return;
                }

                if (!types || types.length === 0) return;

                const chosen = types[selectedIndex];
                if (!chosen || !chosen.id) {
                  alert('Aucun modèle sélectionné.');
                  return;
                }

                try {
                  setBusy(true);

                  // Check if user has living tamago(s)
                  const hasRes = await feedApi.get(`/api/user/${user.id}/has-living-tamago`);
                  const hasLiving = !!hasRes.data;

                  let proceed = true;
                  if (hasLiving) {
                    proceed = window.confirm('Attention — vous avez déjà un tamago en vie. Si vous continuez, il sera perdu. Continuer ?');
                  }

                  if (!proceed) {
                    setBusy(false);
                    return;
                  }

                  // If user has living tamagos, fetch them and kill each
                  if (hasLiving) {
                    const listRes = await feedApi.get(`/api/user/${user.id}/living-tamagos`);
                    type LivingTamago = { id?: number | string };
                    const living: LivingTamago[] = Array.isArray(listRes.data) ? (listRes.data as LivingTamago[]) : [];
                    for (const lt of living) {
                      try {
                        await feedApi.patch(`/api/tamago/${lt.id}`, { kill: true });
                      } catch (e) {
                        console.error('Failed to kill tamago', lt, e);
                        // continue killing others
                      }
                    }
                  }

                  // Create/choose new tamago via authenticated endpoint
                  const payload = { idtype: chosen.id, nom: chosen.nom || chosen.name || `Tamago-${Date.now()}` };
                  await feedApi.post('/api/user/choose-tamago', payload);

                  // On success navigate back to start (or play)
                  router.push('/start');
                } catch (err) {
                  console.error('Failed to choose/create tamago', err);
                  alert('Erreur lors de la création du tamago — réessayez.');
                } finally {
                  setBusy(false);
                }
              }}
              className="px-6 py-2 rounded bg-orange-500 hover:bg-orange-600 text-white"
              disabled={busy}
            >
              {busy ? 'Traitement...' : 'Choisir'}
            </button>
          </div>
        </div>
      )}
    </main>
  );
}
