import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // During development, proxy API calls to the tamagoservice backend to avoid cross-origin
  // cookie issues (SameSite/Secure). This makes browser requests to /api/* hit the Next dev
  // server which forwards them to the backend at localhost:8082.
  async rewrites() {
    // Allow configuring internal service addresses via environment variables.
    // Defaults use Docker Compose service hostnames and internal ports (8080).
    const feedBase = process.env.NEXT_PUBLIC_FEEDSERVICE_BASE || 'http://feedservice:8080';
    const tamagoBase = process.env.NEXT_PUBLIC_API_BASE_URL || process.env.NEXT_PUBLIC_TAMAGOSERVICE_BASE || 'http://tamagoservice:8080';

    return [
      // feedservice endpoints (user / tamago)
      {
        source: '/api/tamago/:path*',
        destination: `${feedBase.replace(/\/+$|$/, '')}/api/tamago/:path*`,
      },
      {
        source: '/api/user/:path*',
        destination: `${feedBase.replace(/\/+$|$/, '')}/api/user/:path*`,
      },
      // fallback to tamagoservice for other api routes
      {
        source: '/api/:path*',
        destination: `${tamagoBase.replace(/\/+$|$/, '')}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;
