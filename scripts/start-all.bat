@echo off
echo ===================================================
echo Starting PrecisionAuto Care Microservices Platform
echo ===================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-21"

echo 1/6 Starting Eureka Server (Port 8761)...
start "Eureka Server" cmd /k "cd eureka-server && ..\mvnw.cmd spring-boot:run"
timeout /t 12 /nobreak >nul

echo 2/6 Starting Auth Service (Port 8081)...
start "Auth Service" cmd /k "cd auth-service && ..\mvnw.cmd spring-boot:run"

echo 3/6 Starting Record Service (Port 8083)...
start "Record Service" cmd /k "cd record-service && ..\mvnw.cmd spring-boot:run"

echo 4/6 Starting Billing Service (Port 8084)...
start "Billing Service" cmd /k "cd billing-service && ..\mvnw.cmd spring-boot:run"

timeout /t 8 /nobreak >nul

echo 5/6 Starting Booking Service (Port 8082)...
start "Booking Service" cmd /k "cd booking-service && ..\mvnw.cmd spring-boot:run"

timeout /t 6 /nobreak >nul

echo 6/6 Starting API Gateway (Port 8080)...
start "API Gateway" cmd /k "cd api-gateway && ..\mvnw.cmd spring-boot:run"

echo ===================================================
echo All microservices launched!
echo Eureka Dashboard: http://localhost:8761
echo API Gateway:      http://localhost:8080
echo Frontend UI:      Open frontend\index.html in browser
echo ===================================================
