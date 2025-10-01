import axios from 'axios';

// Default API (tamagoservice)
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_TAMAGOSERVICE_BASE || process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8082',
  headers: { 'Content-Type': 'application/json' },
});

// Feedservice API (user / tamago endpoints)
const feedApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_FEEDSERVICE_BASE || 'http://localhost:8081',
  headers: { 'Content-Type': 'application/json' },
});

export default api;
export { feedApi };
