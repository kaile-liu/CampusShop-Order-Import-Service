# syntax=docker/dockerfile:1

# ---- build stage -----------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests clean package

# ---- runtime stage ---------------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Default storage location inside the container (override with ORDER_STORAGE_FILE).
ENV ORDER_STORAGE_FILE=/app/data/orders.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
