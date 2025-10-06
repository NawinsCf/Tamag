FROM node:20-bullseye AS builder
WORKDIR /app

# Install dependencies and build
COPY package.json package-lock.json* ./
ARG NPM_TOKEN
RUN corepack enable || true
# If an NPM token is provided as a build-arg, create a minimal .npmrc so private installs work.
# This avoids failing the build when .npmrc is not present in the context.
RUN if [ -n "$NPM_TOKEN" ]; then printf "//registry.npmjs.org/:_authToken=%s\n" "$NPM_TOKEN" > .npmrc; fi
# Try npm ci (preferred) and fall back to npm install if ci fails (e.g., missing lockfile)
RUN npm ci --silent || npm install --no-audit --no-fund --silent

COPY . .
RUN npm run build

FROM node:20-bullseye-slim
WORKDIR /app

# Create non-root user
RUN groupadd -r app && useradd -r -g app app

ENV NODE_ENV=production

# Copy build output and production deps
COPY --from=builder /app/package.json ./
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public

EXPOSE 3000

USER app
CMD ["node", "./node_modules/next/dist/bin/next", "start", "-p", "3000"]
