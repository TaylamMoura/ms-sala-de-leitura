# Usa a imagem oficial do Nginx
FROM nginx:alpine

# Remove a configuração padrão
RUN rm -rf /usr/share/nginx/html/*

# Copia os arquivos do frontend para a pasta servida pelo Nginx
COPY . /usr/share/nginx/html

# Expõe a porta 80
EXPOSE 80

# Comando padrão do Nginx
CMD ["nginx", "-g", "daemon off;"]
