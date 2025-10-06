import axios from 'axios';

// Default API (tamagoservice)
// Prefer explicit NEXT_PUBLIC_API_BASE_URL, fall back to NEXT_PUBLIC_TAMAGOSERVICE_BASE or
// Docker service hostname. Empty string keeps relative paths which allow Next rewrites in dev.
const tamagoBase = process.env.NEXT_PUBLIC_API_BASE_URL || process.env.NEXT_PUBLIC_TAMAGOSERVICE_BASE || '';
const api = axios.create({
  baseURL: tamagoBase,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

// Feedservice API (user / tamago endpoints)
const feedBase = process.env.NEXT_PUBLIC_FEEDSERVICE_BASE || '';
const feedApi = axios.create({
  baseURL: feedBase,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

// Interceptors are registered by importing lib/axios-interceptor which
// attaches to the shared axios instances. Keep this file minimal.

export default api;
export { feedApi };
