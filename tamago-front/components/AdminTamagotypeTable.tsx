 'use client';
import React from 'react';
import { useTamagotypes } from '../lib/hooks/useTamagotypes';
import {
  type ColumnDef,
  flexRender,
  getCoreRowModel,
  useReactTable,
} from '@tanstack/react-table';

type Tamagotype = {
  id: number;
  nom?: string | null;
  descr?: string | null;
  pv?: number | null;
  pf?: number | null;
  nomImg?: string | null;
  estActif?: boolean | null;
};

export default function AdminTamagotypeTable({ initialPage = 0 }: { initialPage?: number }) {
  const [page, setPage] = React.useState(initialPage);
  const [size] = React.useState(20);
  const [q, setQ] = React.useState<string | undefined>(undefined);
  const [queryInput, setQueryInput] = React.useState<string>('');

  const { data, isLoading, error } = useTamagotypes(page, size, q, undefined);
  const pageData: any = data ?? {};

  // debounce search input and reset page to 0 on new query
  React.useEffect(() => {
    const t = setTimeout(() => {
      const newQ = queryInput && queryInput.trim().length > 0 ? queryInput.trim() : undefined;
      // only update if changed
      if (newQ !== q) {
        setPage(0);
        setQ(newQ);
      }
    }, 300);
    return () => clearTimeout(t);
  }, [queryInput]);

  const columns = React.useMemo<ColumnDef<Tamagotype, any>[]>(
    () => [
      {
        accessorKey: 'id',
        header: () => <span className="text-sm font-medium">ID</span>,
      },
      {
        accessorKey: 'nom',
        header: () => <span className="text-sm font-medium">Nom</span>,
      },
      {
        accessorKey: 'descr',
        header: () => <span className="text-sm font-medium">Description</span>,
      },
      {
        accessorKey: 'pv',
        header: () => <span className="text-sm font-medium">PV (max)</span>,
      },
      {
        accessorKey: 'pf',
        header: () => <span className="text-sm font-medium">PF (max)</span>,
      },
      {
        id: 'select',
        header: 'Action',
        cell: ({ row }: { row: any }) => {
          return (
            <button
              type="button"
              className="px-2 py-1 bg-blue-600 text-white rounded"
              onClick={() => {
                // Emit a custom event with the selected tamagotype id so parent can react
                const ev = new CustomEvent('tamagotype:selected', { detail: row.original });
                window.dispatchEvent(ev);
              }}
            >
              Sélectionner
            </button>
          );
        },
      },
    ],
    []
  );

  const table = useReactTable({ data: pageData.content ?? [], columns, getCoreRowModel: getCoreRowModel() });

  return (
    <div>
      <h2 className="text-lg font-semibold mb-2">Liste des Tamagotypes</h2>
      <div className="flex gap-2 mb-2">
        <input
          placeholder="Recherche..."
          className="border px-2 py-1 rounded"
          value={queryInput}
          onChange={(e) => setQueryInput(e.target.value)}
        />
      </div>

      {isLoading && <div>Chargement...</div>}
  {error && <div>Erreur lors du chargement des tamagotypes — vérifiez que le backend est démarré et accessible.</div>}

      <table className="w-full text-left border-collapse">
        <caption className="sr-only">Liste des Tamagotypes (admin)</caption>
        <thead>
          {table.getHeaderGroups().map((hg: any) => (
                <tr key={hg.id}>
                  {hg.headers.map((h: any) => {
                    const headerNode = h.column?.columnDef?.header;
                    const headerName = h.column?.columnDef?.headerName;
                    const rendered = headerNode ? flexRender(headerNode, h.getContext()) : (headerName ?? '');
                    return (
                      <th key={h.id} className="border px-2 py-1 bg-gray-100 text-gray-800">
                        {rendered}
                      </th>
                    );
                  })}
                </tr>
              ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map((row: any) => (
            <tr key={row.id} className="border-t">
              {row.getVisibleCells().map((cell: any) => (
                <td key={cell.id} className="px-2 py-1 align-top">
                  {flexRender(cell.column.columnDef.cell, cell.getContext())}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>

      <div className="mt-2 flex items-center gap-2">
        <button disabled={page <= 0} onClick={() => setPage((p) => Math.max(0, p - 1))} className="px-2 py-1 border rounded">Préc</button>
  <span>Page {pageData ? (pageData.number ?? page) + 1 : page + 1} / {pageData ? pageData.totalPages : '?'}</span>
  <button disabled={pageData ? (pageData.number ?? page) + 1 >= (pageData.totalPages ?? 0) : false} onClick={() => setPage((p) => p + 1)} className="px-2 py-1 border rounded">Suiv</button>
      </div>
    </div>
  );
}
