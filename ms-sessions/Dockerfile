# Base: Imagem leve com JDK 17
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Define o artefato gerado pelo build do Maven para o serviço de sessões
ARG JAR_FILE=target/ms-sessions-*.jar

# Copia o JAR para o container como app.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# Inicializa o microserviço de sessões de leitura
ENTRYPOINT ["java", "-jar", "app.jar"]