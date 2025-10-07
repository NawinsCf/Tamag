import api, { feedApi } from './api';
import * as auth from './auth';

let isRefreshing = false;
let refreshPromise: Promise<unknown> | null = null;

// Simple local redirect guard to avoid immediate redirect loops during
// development or when auth state is in flux. This is a temporary debug
// mitigation and can be removed once the root cause is fixed.
const REDIR_BLOCK_KEY = 'tamago_redirect_blocked';
const REDIR_BLOCK_TTL_MS = 5000; // 5 seconds

function trySetRedirectLock(): boolean {
  try {
    if (typeof window === 'undefined' || !window.localStorage) return true;
    const raw = localStorage.getItem(REDIR_BLOCK_KEY);
    const now = Date.now();
    if (!raw) {
      localStorage.setItem(REDIR_BLOCK_KEY, String(now));
      return true;
    }
    const ts = parseInt(raw, 10) || 0;
    if (now - ts > REDIR_BLOCK_TTL_MS) {
      localStorage.setItem(REDIR_BLOCK_KEY, String(now));
      return true;
    }
    // lock is active
    return false;
  } catch (e) {
    // If localStorage is not available for any reason, fall back to redirect
    return true;
  }
}

export function setupInterceptors(axiosInstance: typeof api) {
  axiosInstance.interceptors.response.use(
    res => res,
    async err => {
      const originalReq = err.config;
      if (!originalReq) return Promise.reject(err);

      // if 401 and not retry yet
      if (err.response && err.response.status === 401 && !originalReq._retry) {
        originalReq._retry = true;

        if (!isRefreshing) {
          isRefreshing = true;
          refreshPromise = auth.refresh().finally(() => {
            isRefreshing = false;
          });
        }

        try {
          await refreshPromise;
          return axiosInstance(originalReq);
        } catch (e) {
          // refresh failed -> redirect to login flow
          try { await auth.logout(); } catch (ignored) {}
          if (typeof window !== 'undefined') {
            // Use a short-lived local lock to avoid immediate redirect loops
            if (trySetRedirectLock()) {
              window.location.href = '/';
            } else {
              // suppress redirect (debug mode) to allow inspection and
              // avoid infinite reloads; the app will still be unauthenticated
              // and the user can retry or inspect cookies.
              // eslint-disable-next-line no-console
              console.warn('Redirect suppressed to avoid loop (temporary)');
            }
          }
          return Promise.reject(e);
        }
      }
      return Promise.reject(err);
    }
  );
}

// attach interceptor to both api and feedApi
setupInterceptors(api);
setupInterceptors(feedApi as unknown as typeof api);

export default setupInterceptors;
