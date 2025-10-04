import api, { feedApi } from '../api';
import { useQuery } from '@tanstack/react-query';

type Tamago = {
  id: number;
  idtype?: number;
  iduser?: number;
  nom?: string;
  pv?: number;
  pf?: number;
  estVivant?: boolean;
};

type PageResp<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
};

export const fetchTamagosPage = async (page = 0, size = 20) => {
  const res = await feedApi.get<PageResp<Tamago>>('/api/tamago/page', { params: { page, size } });
  return res.data;
};

export const useTamagos = (page = 0, size = 20) => {
  return useQuery({
    queryKey: ['tamagos', page, size],
    queryFn: () => fetchTamagosPage(page, size),
    // keepPreviousData typing mismatch in installed @tanstack/react-query types — cast to any
  } as any);
};
