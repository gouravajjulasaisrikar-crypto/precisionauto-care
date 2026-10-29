#!/bin/bash
set -e

echo "==================================================================="
echo "  PRECISIONAUTO CARE - CLOUD MICROSERVICES MESH (RAILWAY CONTAINER)"
echo "==================================================================="

# 1. Launch Netflix Eureka Service Registry
echo "[1/6] Launching Netflix Eureka Registry (Port 8761)..."
java -Xmx80m -Xms32m -jar eureka-server.jar > /dev/null 2>&1 &
echo "Waiting 10s for Eureka to initialize..."
sleep 10

# 2. Launch Auth Service
echo "[2/6] Launching Auth Service (Port 8081)..."
java -Xmx80m -Xms32m -jar auth-service.jar > /dev/null 2>&1 &

# 3. Launch Record Service
echo "[3/6] Launching Record Service (Port 8083)..."
java -Xmx80m -Xms32m -jar record-service.jar > /dev/null 2>&1 &

# 4. Launch Billing Service
echo "[4/6] Launching Billing Service (Port 8084)..."
java -Xmx80m -Xms32m -jar billing-service.jar > /dev/null 2>&1 &
sleep 5

# 5. Launch Booking Service
echo "[5/6] Launching Booking Service (Port 8082)..."
java -Xmx80m -Xms32m -jar booking-service.jar > /dev/null 2>&1 &
sleep 5

# 6. Launch Spring Cloud API Gateway (Binds to Railway's dynamic $PORT or 8080)
GATEWAY_PORT=${PORT:-8080}
echo "[6/6] Launching Spring Cloud API Gateway on Port ${GATEWAY_PORT}..."
echo "==================================================================="
echo "  ALL SERVICES LIVE! GATEWAY LISTENING ON PORT ${GATEWAY_PORT}"
echo "==================================================================="

exec java -Xmx96m -Xms32m -Dserver.port=${GATEWAY_PORT} -jar api-gateway.jar
