# FROM node:22.16.0 AS build
# WORKDIR /app
# COPY package*.json ./

# RUN npm install -g @angular/cli

# RUN npm install
# CMD [ "ng" ,"serve","--host","0.0.0.0" ]
FROM node:22.16.0 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install -g @angular/cli
RUN npm install
COPY . .
CMD ["ng", "serve", "--host", "0.0.0.0"]