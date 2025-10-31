# --- Build stage ---
FROM node:20-alpine AS build
WORKDIR /app

# Install deps (better layer caching)
COPY package.json package-lock.json ./
RUN npm ci

# Copy source and build
COPY . .
# Optional build-time default (can be overridden at runtime)
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
# Emit a build-time env file (runtime can overwrite)
RUN mkdir -p public && \
    echo "window.__ENV__ = { API_BASE_URL: \"${VITE_API_BASE_URL}\" };" > public/env.build.js
RUN npm run build

# --- Runtime stage ---
FROM nginx:1.27-alpine

# Static assets
COPY --from=build /app/dist /usr/share/nginx/html

# Provide env.js for runtime configuration (overrides build-time if env var set)
COPY --from=build /app/public/env.build.js /usr/share/nginx/html/env.js
RUN printf '%s\n' '#!/bin/sh
set -e
if [ -n "$RUNTIME_API_BASE_URL" ]; then
  cat > /usr/share/nginx/html/env.js <<EOT
window.__ENV__ = { API_BASE_URL: "'"$RUNTIME_API_BASE_URL"'" };
EOT
fi' > /docker-entrypoint.d/99-env.sh \
 && chmod +x /docker-entrypoint.d/99-env.sh

# Nginx config: SPA fallback + long cache for assets
RUN printf '%s\n' "server {
  listen 80;
  server_name _;
  root /usr/share/nginx/html;
  index index.html;

  location / {
    try_files \$uri /index.html;
  }

  location ~* \.(js|css|png|jpg|jpeg|gif|svg|woff2?)$ {
    expires 1y;
    add_header Cache-Control \"public, immutable\";
  }
}" > /etc/nginx/conf.d/default.conf
