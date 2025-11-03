FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci --no-audit --no-fund
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
ENV API_BASE_URL="http://localhost:8080"
COPY --from=build /app/dist /usr/share/nginx/html
COPY ops/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY ops/docker-entrypoint.sh /docker-entrypoint.d/99-env.sh
COPY public/env.template.js /env.template.js
RUN chmod +x /docker-entrypoint.d/99-env.sh
EXPOSE 80
CMD ["/docker-entrypoint.sh"]
