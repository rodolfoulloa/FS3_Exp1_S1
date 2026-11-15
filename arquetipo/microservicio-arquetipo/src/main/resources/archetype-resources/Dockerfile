# ===== Etapa 1: compilar el microservicio con Maven =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
# Primero solo el pom.xml para aprovechar la caché de dependencias
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# ===== Etapa 2: imagen liviana solo con el JRE =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
# El wallet de Oracle se monta como volumen en /app/wallet (ver docker-compose.yml)
ENV TNS_ADMIN=/app/wallet
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
