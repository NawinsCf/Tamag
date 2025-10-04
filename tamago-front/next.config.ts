import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // During development, proxy API calls to the tamagoservice backend to avoid cross-origin
  // cookie issues (SameSite/Secure). This makes browser requests to /api/* hit the Next dev
  // server which forwards them to the backend at localhost:8082.
  async rewrites() {
    return [
      // feedservice endpoints (user / tamago)
      {
        source: '/api/tamago/:path*',
        destination: 'http://localhost:8081/api/tamago/:path*',
      },
      {
        source: '/api/user/:path*',
        destination: 'http://localhost:8081/api/user/:path*',
      },
      // fallback to tamagoservice for other api routes
      {
        source: '/api/:path*',
        destination: 'http://localhost:8082/api/:path*',
      },
    ];
  },
};

export default nextConfig;
