import axios from 'axios';

// Default API (tamagoservice)
const api = axios.create({
  // Use relative base so the Next dev server can proxy /api requests to the backend.
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || '',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

// Feedservice API (user / tamago endpoints)
const feedApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_FEEDSERVICE_BASE || '',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

// Interceptors are registered by importing lib/axios-interceptor which
// attaches to the shared axios instances. Keep this file minimal.

export default api;
export { feedApi };
