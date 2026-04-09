# Base image
FROM nginx:alpine

# Limpa o diretório padrão do servidor
RUN rm -rf /usr/share/nginx/html/*

# Copia os recursos do frontend para o container
COPY ./http/ /usr/share/nginx/html
COPY http/js/ /usr/share/nginx/html/js/
COPY http/img/ /usr/share/nginx/html/img/

EXPOSE 80

# Inicializa o Nginx
CMD ["nginx", "-g", "daemon off;"]
