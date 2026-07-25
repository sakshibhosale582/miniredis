FROM node:22-alpine AS frontend-build

WORKDIR /frontend

COPY mini-redis-ui/package.json mini-redis-ui/package-lock.json ./
RUN npm ci

COPY mini-redis-ui/ ./
RUN npm run build

FROM maven:3.9.9-eclipse-temurin-17 AS backend-build

WORKDIR /build

COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
COPY --from=frontend-build /frontend/dist ./src/main/resources/static
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring \
    && mkdir -p /app/data \
    && chown -R spring:spring /app

COPY --from=backend-build --chown=spring:spring /build/target/mini-redis-1.0.0.jar app.jar

USER spring

ENV PORT=8080 \
    CACHE_PERSISTENCE_FILE=/app/data/mini-redis-cache.dat

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
