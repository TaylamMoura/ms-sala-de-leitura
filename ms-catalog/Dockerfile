# 1. Base: Imagem leve com JDK 17
FROM eclipse-temurin:17-jdk-alpine

# 2. Diretório de trabalho no container
WORKDIR /app

# 3. Argumento para pegar o JAR compilado
ARG JAR_FILE=target/ms-catalog-*.jar

# 4. Copia o JAR compilado para dentro do container
COPY ${JAR_FILE} app.jar

# 5. Expõe a porta (Opcional, mas boa prática)
EXPOSE 8080

# 6. Comando para rodar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]