FROM node:20-bullseye AS builder
WORKDIR /app

# Install dependencies and build
COPY package.json package-lock.json* ./
COPY .npmrc .npmrc
RUN corepack enable || true
RUN npm ci --silent

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
