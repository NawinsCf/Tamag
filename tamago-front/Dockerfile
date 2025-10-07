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
# Allow build-time environment variables to be injected so Next's build can bake
# correct values for rewrites and NEXT_PUBLIC_* variables. These are provided
# as --build-arg when building the image (or via docker-compose build args).
# Define ARGs so they can be passed in, then promote them to ENV so `npm run build`
# sees them in process.env during the build step.
ARG NEXT_PUBLIC_FEEDSERVICE_BASE=""
ARG NEXT_PUBLIC_API_BASE_URL=""
ARG NEXT_PUBLIC_TAMAGOSERVICE_BASE=""
ARG INTERNAL_FEEDSERVICE_BASE=""
ARG INTERNAL_TAMAGOSERVICE_BASE=""

ENV NEXT_PUBLIC_FEEDSERVICE_BASE=${NEXT_PUBLIC_FEEDSERVICE_BASE}
ENV NEXT_PUBLIC_API_BASE_URL=${NEXT_PUBLIC_API_BASE_URL}
ENV NEXT_PUBLIC_TAMAGOSERVICE_BASE=${NEXT_PUBLIC_TAMAGOSERVICE_BASE}
ENV INTERNAL_FEEDSERVICE_BASE=${INTERNAL_FEEDSERVICE_BASE}
ENV INTERNAL_TAMAGOSERVICE_BASE=${INTERNAL_TAMAGOSERVICE_BASE}

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
