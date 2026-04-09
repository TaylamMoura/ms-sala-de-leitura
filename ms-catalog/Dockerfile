# Base: JDK 17 sobre Alpine
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Define o artefato gerado pelo build do Maven
ARG JAR_FILE=target/ms-catalog-*.jar

# Copia o JAR para o container como app.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# Inicializa o microserviço de catálogo
ENTRYPOINT ["java", "-jar", "app.jar"]