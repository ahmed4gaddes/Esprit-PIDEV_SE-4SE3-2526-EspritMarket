# Angular 17: production build, then nginx. Strips http://localhost:8081 so the browser
# calls the same origin and nginx proxies to the backend (see nginx.conf).
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production

# Remove hardcoded API host so requests are same-origin (/api/..., /Store, ...)
RUN find /app/dist -type f \( -name '*.js' -o -name '*.mjs' \) -exec sed -i 's|http://localhost:8081||g' {} +

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/esprit-market-frontend/browser /usr/share/nginx/html

EXPOSE 80
