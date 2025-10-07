import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // During development, proxy API calls to the tamagoservice backend to avoid cross-origin
  // cookie issues (SameSite/Secure). This makes browser requests to /api/* hit the Next dev
  // server which forwards them to the backend at localhost:8082.
  async rewrites() {
    // Allow configuring internal service addresses via environment variables.
    // When env vars are not present, avoid rewriting /api to itself which causes an infinite loop.
    // Only apply localhost fallbacks in development to preserve the previous dev behavior.
  // Use INTERNAL_* env vars for server-side rewrites (set in docker-compose.prod.yml).
  const feedBaseEnv = process.env.INTERNAL_FEEDSERVICE_BASE;
  const tamagoBaseEnv = process.env.INTERNAL_TAMAGOSERVICE_BASE || process.env.INTERNAL_API_BASE_URL;

    const rewrites: Array<{ source: string; destination: string }> = [];

    if (feedBaseEnv) {
      const feedBase = feedBaseEnv.replace(/\/+$/g, '');
      rewrites.push({ source: '/api/tamago/:path*', destination: `${feedBase}/api/tamago/:path*` });
      rewrites.push({ source: '/api/user/:path*', destination: `${feedBase}/api/user/:path*` });
    } else if (process.env.NODE_ENV !== 'production') {
      // dev fallback to localhost so local dev workflow keeps working
      rewrites.push({ source: '/api/tamago/:path*', destination: 'http://localhost:8081/api/tamago/:path*' });
      rewrites.push({ source: '/api/user/:path*', destination: 'http://localhost:8081/api/user/:path*' });
    }

    if (tamagoBaseEnv) {
      const tamagoBase = tamagoBaseEnv.replace(/\/+$/g, '');
      // Only add the tamagoservice fallback if it won't rewrite to the same path
      rewrites.push({ source: '/api/:path*', destination: `${tamagoBase}/api/:path*` });
    } else if (process.env.NODE_ENV !== 'production') {
      rewrites.push({ source: '/api/:path*', destination: 'http://localhost:8082/api/:path*' });
    }

    return rewrites;
  },
};

export default nextConfig;
