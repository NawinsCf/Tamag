'use client';
import React from 'react';
import { useTamagos } from '../lib/hooks/useTamagos';
import { ColumnDef, flexRender, getCoreRowModel, useReactTable } from '@tanstack/react-table';

type Tamago = {
  id: number;
  nom?: string | null;
  pv?: number | null;
  pf?: number | null;
  idtype?: number | null;
  estVivant?: boolean | null;
};

export default function AdminTamagoTable({ initialPage = 0 }: { initialPage?: number }) {
  const [page, setPage] = React.useState(initialPage);
  const [size] = React.useState(20);

  const { data, isLoading, error } = useTamagos(page, size);
  const pageData: any = data ?? {};

  const columns = React.useMemo<ColumnDef<Tamago, any>[]>(
    () => [
  { accessorKey: 'id', header: () => <span className="text-sm font-medium">ID</span> },
  { accessorKey: 'nom', header: () => <span className="text-sm font-medium">Nom</span> },
  { accessorKey: 'pv', header: () => <span className="text-sm font-medium">PV (actuels)</span> },
  { accessorKey: 'pf', header: () => <span className="text-sm font-medium">PF (actuels)</span> },
  { accessorKey: 'idtype', header: () => <span className="text-sm font-medium">Tamagotype (ID)</span> },
      {
        id: 'select',
        header: 'Action',
        cell: ({ row }: { row: any }) => (
          <button
            type="button"
            className="px-2 py-1 bg-blue-600 text-white rounded"
            onClick={() => {
              const ev = new CustomEvent('tamago:selected', { detail: row.original });
              window.dispatchEvent(ev);
            }}
          >Sélectionner</button>
        ),
      },
    ],
    []
  );

  const table = useReactTable({ data: pageData.content ?? [], columns, getCoreRowModel: getCoreRowModel() });

  return (
    <div>
      <h2 className="text-lg font-semibold mb-2">Liste des Tamagos</h2>
      {isLoading && <div>Chargement...</div>}
      {error && (
        <div>
          <div>Erreur lors du chargement des tamagos</div>
          <pre className="text-sm mt-2">{JSON.stringify(error, null, 2)}</pre>
        </div>
      )}
      <table className="w-full text-left border-collapse">
        <caption className="sr-only">Liste des Tamagos (admin)</caption>
        <thead>
          {table.getHeaderGroups().map((hg: any) => (
            <tr key={hg.id}>
              {hg.headers.map((h: any) => {
                const headerNode = h.column?.columnDef?.header;
                const headerName = h.column?.columnDef?.headerName;
                const rendered = headerNode ? flexRender(headerNode, h.getContext()) : (headerName ?? '');
                return (
                  <th key={h.id} className="border px-2 py-1 bg-gray-100 text-gray-800">{rendered}</th>
                );
              })}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map((row: any) => (
            <tr key={row.id} className="border-t">
              {row.getVisibleCells().map((cell: any) => (
                <td key={cell.id} className="px-2 py-1 align-top">{flexRender(cell.column.columnDef.cell, cell.getContext())}</td>
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
