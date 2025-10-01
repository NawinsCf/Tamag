"use client";

import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../../src/context/AuthContext';
import { useRouter } from 'next/navigation';
import api, { feedApi } from '../../lib/api';
import dynamic from 'next/dynamic';
import Toast from '../../components/Toast';
const CreateTamagotypeModal = dynamic(() => import('../../components/CreateTamagotypeModal'), { ssr: false });
// Carousel removed import here because it's not used on the Start page by default
const Ctamag = dynamic(() => import('../../components/Ctamag'), { ssr: false });

export default function StartPage() {
  const { user, initialized } = useContext(AuthContext);
  const router = useRouter();
  const [hasLivingTamago, setHasLivingTamago] = useState<boolean | null>(null);
  type LivingTamago = { id?: number | string; pv?: number | null; pf?: number | null; nom?: string; idtype?: number | string; estVivant?: boolean };
  const [livingTamago, setLivingTamago] = useState<LivingTamago | null>(null);
  type TamagoType = { nomImg?: string | null; image?: string | null; nom?: string | null; PV?: number | null; PF?: number | null; pv?: number | null; pf?: number | null; valueFaim?: number | null; valueRegen?: number | null } | null;
  const [tamagotype, setTamagotype] = useState<TamagoType>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  // showTamago controls whether the Tamago card and periodic updater are active
  const [showTamago, setShowTamago] = useState(false);
  const [toastMsg, setToastMsg] = useState<string | null>(null);
  const [toastType, setToastType] = useState<'info'|'success'|'error'>('info');
  const [feeding, setFeeding] = useState(false);

  useEffect(() => {
    // only redirect to index when auth init finished and there's no user
    if (initialized && !user) {
      router.replace('/');
    }
  }, [user, initialized, router]);

  useEffect(() => {
    // after auth is initialized and we have a user, check if they have a living tamago
    if (!initialized || !user) return;
    let mounted = true;
    (async () => {
      try {
        const res = await feedApi.get(`/api/user/${user.id}/has-living-tamago`);
        if (!mounted) return;
        const has = Boolean(res.data);
        setHasLivingTamago(has);

        if (!has) return;

        // Fetch the living tamagos (we'll take the first)
        const listRes = await feedApi.get(`/api/user/${user.id}/living-tamagos`);
  const list: LivingTamago[] = Array.isArray(listRes.data) ? (listRes.data as LivingTamago[]) : [];
        if (!mounted || list.length === 0) return;
        const tam = list[0];

        // Call calculefaim to bring tamago up-to-date (controller returns the updated TamagoResponse)
        try {
          const calcRes = await feedApi.post(`/api/tamago/${tam.id}/calculefaim`);
          if (mounted && calcRes && calcRes.data) {
            const data = calcRes.data as Partial<LivingTamago & { idtype?: number | string; estVivant?: boolean }>;
            tam.pv = data.pv ?? tam.pv;
            tam.pf = data.pf ?? tam.pf;
            tam.nom = data.nom ?? tam.nom;
            tam.idtype = data.idtype ?? tam.idtype;
            tam.estVivant = data.estVivant ?? tam.estVivant;
          }
        } catch (err) {
          // If calculefaim fails, continue with the tamago we have
          console.error('calculefaim failed', err);
        }

        // Fetch the tamagotype details from tamagoservice
          try {
            const typeRes = await api.get(`/api/tamagotype/${tam.idtype}`);
            if (mounted) setTamagotype(typeRes.data as TamagoType);
          } catch (_err) {
            console.error('Failed to load tamagotype for living tamago', _err);
            if (mounted) setTamagotype(null);
          }

        if (mounted) setLivingTamago(tam);
      } catch (e) {
        // on error, assume no living tamago to be safe
        if (mounted) setHasLivingTamago(false);
      }
    })();
    return () => {
      mounted = false;
    };
  }, [initialized, user]);

  // Front-end-only periodic updater: use refs so the interval is created once and
  // runs strictly every minute. We avoid re-creating the interval when state updates
  // (which previously caused the tick to effectively run immediately repeatedly).
  const livingRef = React.useRef(livingTamago);
  const typeRef = React.useRef(tamagotype);

  // keep refs up-to-date when state changes
  useEffect(() => { livingRef.current = livingTamago; }, [livingTamago]);
  useEffect(() => { typeRef.current = tamagotype; }, [tamagotype]);

  // Periodic updater: only active when showTamago is true. It updates pf/pv every minute.
  useEffect(() => {
    let mounted = true;
    if (!showTamago) return () => { mounted = false; };

    const tick = async () => {
      const current = livingRef.current;
      const currentType = typeRef.current;
      if (!mounted || !current || !currentType) return;

      // determine whether the tamago is considered "alive" for the client-side updater
      const isAlive = (current.estVivant ?? true) !== false && (Number(current.pv ?? 0) > 0);
      if (!isAlive) return;

      const valueFaim = Number(currentType.valueFaim ?? (currentType as any).value_faim ?? 1);
      const valueRegen = Number(currentType.valueRegen ?? (currentType as any).value_regen ?? 1);

      const idTam = current.id;
      if (idTam === undefined || idTam === null) return;

      let pf = Number(current.pf ?? 0);
      let pv = Number(current.pv ?? 0);

      if (pf > 0) {
        pf = Math.max(0, pf - valueFaim);
      } else if (pv > 0) {
        pv = Math.max(0, pv - valueRegen);
      }

      // update local state (no persistence)
      setLivingTamago(prev => prev ? { ...prev, pf, pv } : prev);

      // if pv reached zero call calculefaim to get authoritative state
      if (pv <= 0) {
        try {
          const res = await feedApi.post(`/api/tamago/${idTam}/calculefaim`);
          if (res && res.data) {
            const data = res.data as Partial<LivingTamago & { estVivant?: boolean }>;
            setLivingTamago(prev => prev ? { ...prev, pv: data.pv ?? prev.pv, pf: data.pf ?? prev.pf, estVivant: data.estVivant ?? prev.estVivant } : prev);
            if (data.estVivant === false) {
              setToastType('error');
              setToastMsg('Ton Tamago est mort.');
            }
          }
        } catch (err) {
          console.error('calculefaim (periodic) failed', err);
        }
      }
    };

    // create a single interval that runs every minute; do NOT run tick immediately
    const intervalId = setInterval(tick, 60_000);
    // store interval id in ref so we can stop it from other handlers
    intervalRef.current = intervalId as unknown as number;
    return () => { mounted = false; clearInterval(intervalId); intervalRef.current = null; };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [showTamago]);

  // Control to stop/start the periodic updater when showing modal or leaving page
  const intervalRef = React.useRef<number | null>(null);

  // stopAndFlush: clear interval and call calculefaim once to persist/refresh state
  const stopAndFlush = async () => {
    const id = intervalRef.current;
    if (id) {
      clearInterval(id);
      intervalRef.current = null;
    }
    const current = livingRef.current;
    if (!current || current.id === undefined || current.id === null) return;
    try {
      const res = await feedApi.post(`/api/tamago/${current.id}/calculefaim`);
      if (res && res.data) {
        const data = res.data as Partial<LivingTamago & { estVivant?: boolean }>;
        setLivingTamago(prev => prev ? { ...prev, pv: data.pv ?? prev.pv, pf: data.pf ?? prev.pf, estVivant: data.estVivant ?? prev.estVivant } : prev);
        if (data.estVivant === false) {
          setToastType('error');
          setToastMsg('Ton Tamago est mort.');
        }
      }
    } catch (err) {
      console.error('calculefaim (stopAndFlush) failed', err);
    }
  };

  // restart the interval if conditions are met (used after closing modal)
  const startPeriodicIfNeeded = () => {
    // if already running, do nothing
    if (intervalRef.current) return;
    const current = livingRef.current;
    const currentType = typeRef.current;
    if (!showTamago) return;
    if (!current || !currentType) return;
    const isAlive = (current.estVivant ?? true) !== false && (Number(current.pv ?? 0) > 0);
    if (!isAlive) return;
    const id = setInterval(async () => {
      // delegate to existing tick logic by calling the same body
      const currentInner = livingRef.current;
      const currentTypeInner = typeRef.current;
      if (!currentInner || !currentTypeInner) return;
      const valueFaim = Number(currentTypeInner.valueFaim ?? (currentTypeInner as any).value_faim ?? 1);
      const valueRegen = Number(currentTypeInner.valueRegen ?? (currentTypeInner as any).value_regen ?? 1);
      let pf = Number(currentInner.pf ?? 0);
      let pv = Number(currentInner.pv ?? 0);
      if (pf > 0) pf = Math.max(0, pf - valueFaim);
      else if (pv > 0) pv = Math.max(0, pv - valueRegen);
      setLivingTamago(prev => prev ? { ...prev, pf, pv } : prev);
      if (pv <= 0) {
        try {
          const res = await feedApi.post(`/api/tamago/${currentInner.id}/calculefaim`);
          if (res && res.data) {
            const data = res.data as Partial<LivingTamago & { estVivant?: boolean }>;
            setLivingTamago(prev => prev ? { ...prev, pv: data.pv ?? prev.pv, pf: data.pf ?? prev.pf, estVivant: data.estVivant ?? prev.estVivant } : prev);
            if (data.estVivant === false) {
              setToastType('error');
              setToastMsg('Ton Tamago est mort.');
            }
          }
        } catch (err) {
          console.error('calculefaim (periodic) failed', err);
        }
      }
    }, 60_000);
    intervalRef.current = id as unknown as number;
  };

  // Stop periodic updater when modal opens, and restart when it closes
  useEffect(() => {
    if (showCreateModal) {
      // stop and flush when opening modal
      void stopAndFlush();
    } else {
      // modal closed: start if needed
      startPeriodicIfNeeded();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [showCreateModal]);

  // when leaving the page (unmount), stop and flush
  useEffect(() => {
    return () => {
      void stopAndFlush();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // while we don't yet know if a user is present, don't render or redirect
  if (!initialized) return null;

  if (!user) return null;

  return (
    <main className="p-6">
      <h1 className="text-2xl font-bold">Bienvenue {user.pseudo}</h1>
      <p className="mt-4 mb-6">Ceci est la page start accessible après connexion.</p>

  <section className="flex flex-wrap gap-2 items-center justify-center w-full max-w-3xl mx-auto">
        {/* Public buttons */}
          <button
            aria-label="Jouer"
            onClick={() => {
              // guard: don't show if no living tamago
              if (!hasLivingTamago) return;
              // reveal the Tamago and start periodic updater
              setShowTamago(true);
            }}
          aria-disabled={!hasLivingTamago}
          disabled={!hasLivingTamago}
          className={`flex-shrink-0 max-w-[150px] py-4 pl-4 pr-3 text-sm rounded mx-1 ${
            hasLivingTamago
              ? 'bg-emerald-600 hover:bg-emerald-700 text-white'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed opacity-70'
          }`}
        >
          Jouer
        </button>

        <button
          aria-label="Choisir un tamago"
          onClick={() => router.push('/choose-tamago')}
          className="flex-shrink-0 max-w-[150px] py-4 pl-4 pr-3 text-sm rounded bg-sky-600 hover:bg-sky-700 text-white mx-1"
        >
          Choisir un tamago
        </button>

        {/* other top menu buttons... (Nourrir moved below the card) */}

        {/* Admin-only buttons */}
        {user.estAdmin && (
          <>
            <button
              aria-label="Créer un modèle"
              onClick={() => setShowCreateModal(true)}
              className="flex-shrink-0 max-w-[150px] py-4 pl-4 pr-3 text-sm rounded border border-gray-300 bg-white hover:bg-gray-50 text-gray-800 mx-1"
            >
              Créer un Model
            </button>

            <button
              aria-label="Voir les tamagoType"
              onClick={() => router.push('/admin/tamago-types')}
              className="flex-shrink-0 max-w-[150px] py-4 pl-4 pr-3 text-sm rounded border border-gray-300 bg-white hover:bg-gray-50 text-gray-800 mx-1"
            >
              Voir les tamagoType
            </button>

            <button
              aria-label="Voir tous les tamago"
              onClick={() => router.push('/admin/tamago-list')}
              className="flex-shrink-0 max-w-[150px] py-4 pl-4 pr-3 text-sm rounded border border-gray-300 bg-white hover:bg-gray-50 text-gray-800 mx-1"
            >
              Voir tous les tamago
            </button>
          </>
        )}
      </section>
      {showCreateModal && (
        <CreateTamagotypeModal
          onClose={() => setShowCreateModal(false)}
          onCreated={() => { setToastType('success'); setToastMsg('Tamagotype créé'); }}
        />
      )}
      {toastMsg && <Toast message={toastMsg} type={toastType} onClose={() => setToastMsg(null)} />}

      {/* If user has a living tamago, show its card below the menu */}
  {showTamago && livingTamago && tamagotype && !showCreateModal && (
        <div className="max-w-3xl mx-auto mt-8">
          <Ctamag
            label={livingTamago.nom || `Tamago #${livingTamago.id}`}
            imageSrc={(tamagotype?.nomImg || tamagotype?.image) ?? undefined}
            imageAlt={tamagotype?.nom || 'Tamago image'}
            tamago={{ pv: livingTamago.pv ?? null, pf: livingTamago.pf ?? null, id: livingTamago.id, nom: livingTamago.nom ?? null }}
            tamagoType={{ PV: tamagotype?.PV ?? tamagotype?.pv ?? null, PF: tamagotype?.PF ?? tamagotype?.pf ?? null, nomImg: tamagotype?.nomImg ?? tamagotype?.image ?? null, image: tamagotype?.image ?? null }}
            size="md"
          />
          {/* Nourrir button placed below the card, centered */}
          <div className="mt-4 flex justify-center">
            {livingTamago && (livingTamago.estVivant ?? true) && (
              <button
                aria-label="Nourrir"
                onClick={async () => {
                  if (!livingTamago || !livingTamago.id) return;
                  setFeeding(true);
                  try {
                    // Call nourrir endpoint (may return 204 No Content)
                    await feedApi.post(`/api/tamago/${livingTamago.id}/nourrir`);

                    // Fetch authoritative updated tamago state
                    try {
                      const calc = await feedApi.post(`/api/tamago/${livingTamago.id}/calculefaim`);
                      if (calc && calc.data) {
                        const data = calc.data as Partial<LivingTamago & { estVivant?: boolean }>;
                        setLivingTamago(prev => prev ? { ...prev, pv: data.pv ?? prev.pv, pf: data.pf ?? prev.pf, estVivant: data.estVivant ?? prev.estVivant } : prev);
                        setToastType('success');
                        setToastMsg('Tamago nourri');
                      } else {
                        setToastType('info');
                        setToastMsg('Nourriture envoyée');
                      }
                    } catch (err) {
                      console.error('calculefaim after nourrir failed', err);
                      setToastType('info');
                      setToastMsg('Nourriture envoyée (mise à jour impossible)');
                    }
                  } catch (err) {
                    console.error('nourrir failed', err);
                    setToastType('error');
                    setToastMsg('Impossible de nourrir le Tamago');
                  } finally {
                    setFeeding(false);
                  }
                }}
                disabled={feeding}
                className={`py-2 px-4 text-sm rounded ${feeding ? 'bg-yellow-300 text-gray-700 cursor-not-allowed' : 'bg-yellow-500 hover:bg-yellow-600 text-white'}`}
              >
                {feeding ? 'Nourrir...' : 'Nourrir'}
              </button>
            )}
          </div>
        </div>
      )}

      {/* Carousel preview removed from Start page by default */}
    </main>
  );
}
