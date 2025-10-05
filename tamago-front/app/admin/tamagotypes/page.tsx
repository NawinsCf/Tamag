'use client';
import React from 'react';
import { useRouter } from 'next/navigation';
import AdminTamagotypeTable from '../../../components/AdminTamagotypeTable';
import EditTamagotypeModal from '../../../components/EditTamagotypeModal';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

export default function AdminTamagotypesPage() {
  const qcRef = React.useRef<QueryClient | null>(null);
  if (!qcRef.current) qcRef.current = new QueryClient();
  const router = useRouter();
  const [selectedId, setSelectedId] = React.useState<number | null>(null);
  React.useEffect(() => {
    const handler = (e: any) => {
      const t = e.detail as any;
      console.log('tamagotype selected', t);
      if (t && t.id) setSelectedId(t.id);
    };
    window.addEventListener('tamagotype:selected', handler as EventListener);
    return () => window.removeEventListener('tamagotype:selected', handler as EventListener);
  }, []);

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold mb-4">Administration - Tamagotypes</h1>
      <QueryClientProvider client={qcRef.current}>
        {/* Retour button above the table */}
        <div className="mb-4">
          <button
            type="button"
            onClick={() => router.push('/start')}
            className="px-3 py-2 rounded border bg-white hover:bg-gray-50 text-gray-800"
          >
            ← Retour
          </button>
        </div>
        <AdminTamagotypeTable />
        {selectedId !== null && (
          <EditTamagotypeModal
            id={selectedId}
            onClose={() => setSelectedId(null)}
            onUpdated={() => {
              // invalidate the tamagotypes query so table refetches
              try {
                qcRef.current?.invalidateQueries?.({ queryKey: ['tamagotypes'] } as any);
              } catch (e) {
                // best-effort
                console.warn('failed to invalidate', e);
              }
              setSelectedId(null);
            }}
          />
        )}
      </QueryClientProvider>
    </div>
  );
}
