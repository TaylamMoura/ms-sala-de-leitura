# Base: Imagem leve com JDK 17
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Define o caminho do artefato gerado pelo Maven
ARG JAR_FILE=target/ms-auth-*.jar

# Copia o JAR para o container como app.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# Inicializa a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]