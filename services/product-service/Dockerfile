# Estágio 1: Build (Compila o código Java)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Isto faz download das dependências primeiro (mais rápido)
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Imagem final leve)
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copia apenas o jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]