FROM node:20.15 AS build

WORKDIR /app

COPY package*.json .

RUN npm install --frozen-lockfile

COPY . .

RUN npm run build

FROM nginx:latest

COPY --from=build /app/dist /usr/share/nginx/html
COPY conf.d /etc/nginx/conf.d

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]