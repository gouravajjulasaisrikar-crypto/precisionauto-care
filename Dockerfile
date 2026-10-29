# ==============================================================================
# PrecisionAuto Care - Multi-Service Cloud Mesh Dockerfile for Railway
# Builds and containerizes Eureka, Gateway, Auth, Booking, Record, and Billing
# ==============================================================================

# Stage 1: Build all Spring Boot Microservices with Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# Copy POM files for dependency resolution
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY auth-service/pom.xml auth-service/
COPY booking-service/pom.xml booking-service/
COPY record-service/pom.xml record-service/
COPY billing-service/pom.xml billing-service/

# Copy all module source trees
COPY eureka-server/src eureka-server/src
COPY api-gateway/src api-gateway/src
COPY auth-service/src auth-service/src
COPY booking-service/src booking-service/src
COPY record-service/src record-service/src
COPY billing-service/src billing-service/src

# Build production JARs (skipping tests for fast build)
RUN mvn clean package -DskipTests

# Stage 2: Production JRE 21 Runtime Container
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl bash

# Copy pre-packaged executable JARs
COPY --from=builder /workspace/eureka-server/target/*.jar ./eureka-server.jar
COPY --from=builder /workspace/api-gateway/target/*.jar ./api-gateway.jar
COPY --from=builder /workspace/auth-service/target/*.jar ./auth-service.jar
COPY --from=builder /workspace/booking-service/target/*.jar ./booking-service.jar
COPY --from=builder /workspace/record-service/target/*.jar ./record-service.jar
COPY --from=builder /workspace/billing-service/target/*.jar ./billing-service.jar

# Copy and configure startup orchestrator
COPY entrypoint.sh ./
RUN chmod +x ./entrypoint.sh

# Expose Railway Public Port (Default 8080)
EXPOSE 8080 8761

ENTRYPOINT ["./entrypoint.sh"]
