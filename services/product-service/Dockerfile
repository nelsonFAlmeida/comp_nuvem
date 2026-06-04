# Stage 1 – Build (compila o código Java)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Faz download das dependências primeiro (mais rápido)
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2 – Runtime (imagem final leve)
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copia apenas o jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]
