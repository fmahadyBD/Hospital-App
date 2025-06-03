# Development Dockerfile - HOT RELOAD ENABLED
FROM node:22.16.0
WORKDIR /app
COPY package*.json ./
RUN npm install -g @angular/cli
RUN npm install
# ng serve with hot reload
CMD ["ng", "serve", "--host", "0.0.0.0", "--poll", "2000"]