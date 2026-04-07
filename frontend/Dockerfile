# Usa a imagem oficial do Nginx
FROM nginx:alpine

# Remove a configuração padrão
RUN rm -rf /usr/share/nginx/html/*

# Copia os arquivos do frontend para a pasta servida pelo Nginx
COPY ./http/ /usr/share/nginx/html
COPY http/js/ /usr/share/nginx/html/js/
COPY http/img/ /usr/share/nginx/html/img/

# Expõe a porta 80
EXPOSE 80

# Comando padrão do Nginx
CMD ["nginx", "-g", "daemon off;"]
