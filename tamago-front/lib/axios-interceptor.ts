import api, { feedApi } from './api';
import * as auth from './auth';

let isRefreshing = false;
let refreshPromise: Promise<unknown> | null = null;

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
          if (typeof window !== 'undefined') window.location.href = '/';
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
