import api from './api';

export async function login(pseudo: string, mdp: string) {
  // POST to login - server will set HttpOnly cookies
  return api.post('/api/auth/login', { pseudo, mdp });
}

export async function logout() {
  return api.post('/api/auth/logout');
}

export async function me() {
  return api.get('/api/auth/me');
}

export async function refresh() {
  return api.post('/api/auth/refresh');
}
