import axios from 'axios';
import api from '../api';
import { useQuery, type UseQueryResult } from '@tanstack/react-query';

type Tamagotype = {
  id: number;
  nom?: string | null;
  descr?: string | null;
  pv?: number | null;
  pf?: number | null;
  nomImg?: string | null;
  couleur?: string | null;
  valueFaim?: number | null;
  valueRegen?: number | null;
  estActif?: boolean | null;
};

type PageResp<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // page index
};

export const fetchTamagotypesPage = async (page = 0, size = 20, q?: string, sort?: string) => {
  const params: any = { page, size };
  if (q) params.q = q;
  if (sort) params.sort = sort;
  const res = await api.get<PageResp<Tamagotype>>('/api/tamagotype/page', { params });
  return res.data;
};

export const useTamagotypes = (page = 0, size = 20, q?: string, sort?: string): any => {
  // React Query v5 typing surface can be strict; cast to any to keep the hook ergonomic for this project.
  return (useQuery({
    queryKey: ['tamagotypes', page, size, q, sort],
    queryFn: () => fetchTamagotypesPage(page, size, q, sort),
    staleTime: 1000 * 60, // 1 min
  }) as unknown) as any;
};
