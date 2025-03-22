FROM nginx:alpine
COPY ../FrontEnd/distAdmin /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]