FROM nginx:alpine
COPY ../FrontEnd/distUser /usr/share/nginx/html
# EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]