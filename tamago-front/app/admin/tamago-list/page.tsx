'use client';
import React from 'react';
import { useRouter } from 'next/navigation';
import AdminTamagoTable from '../../../components/AdminTamagoTable';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

export default function AdminTamagoListPage() {
  const qcRef = React.useRef<QueryClient | null>(null);
  if (!qcRef.current) qcRef.current = new QueryClient();
  const router = useRouter();

  const [selectedTamago, setSelectedTamago] = React.useState<any | null>(null);

  React.useEffect(() => {
    const handler = (e: any) => {
      setSelectedTamago(e.detail);
    };
    window.addEventListener('tamago:selected', handler as EventListener);
    return () => window.removeEventListener('tamago:selected', handler as EventListener);
  }, []);

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold mb-4">Administration - Tamagos</h1>
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
      <QueryClientProvider client={qcRef.current}>
        <AdminTamagoTable />
      </QueryClientProvider>
      {selectedTamago && (
        <div className="mt-4 p-4 border rounded bg-white">
          <h2 className="font-semibold">Tamago sélectionné</h2>
          <pre className="text-sm">{JSON.stringify(selectedTamago, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}
