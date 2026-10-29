@echo off
title PrecisionAuto Care - Master Startup
color 0A
echo ===================================================================
echo     PRECISIONAUTO CARE - 1-CLICK REVIEW DAY STARTUP ENGINE
echo ===================================================================
echo.
echo Setting Java 21 LTS environment and memory limits...
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo [1/7] Launching Netflix Eureka Service Registry (Port 8761)...
start "Eureka-Server-8761" cmd /k "title Eureka Server (8761) && cd /d "%~dp0eureka-server" && "%JAVA_HOME%\bin\java.exe" -Xmx128m -jar target\eureka-server-1.0.0.jar"
echo Waiting 10 seconds for Eureka registry initialization...
timeout /t 10 /nobreak >nul

echo.
echo [2/7] Launching Auth Service (Port 8081)...
start "Auth-Service-8081" cmd /k "title Auth Service (8081) && cd /d "%~dp0auth-service" && "%JAVA_HOME%\bin\java.exe" -Xmx128m -jar target\auth-service-1.0.0.jar"

echo.
echo [3/7] Launching Record Service (Port 8083)...
start "Record-Service-8083" cmd /k "title Record Service (8083) && cd /d "%~dp0record-service" && "%JAVA_HOME%\bin\java.exe" -Xmx128m -jar target\record-service-1.0.0.jar"

echo.
echo [4/7] Launching Billing Service (Port 8084)...
start "Billing-Service-8084" cmd /k "title Billing Service (8084) && cd /d "%~dp0billing-service" && "%JAVA_HOME%\bin\java.exe" -Xmx128m -jar target\billing-service-1.0.0.jar"
timeout /t 6 /nobreak >nul

echo.
echo [5/7] Launching Booking Service (Port 8082)...
start "Booking-Service-8082" cmd /k "title Booking Service (8082) && cd /d "%~dp0booking-service" && "%JAVA_HOME%\bin\java.exe" -Xmx128m -jar target\booking-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

echo.
echo [6/7] Launching API Gateway with JWT Filter (Port 8080)...
start "API-Gateway-8080" cmd /k "title API Gateway (8080) && cd /d "%~dp0api-gateway" && "%JAVA_HOME%\bin\java.exe" -Xmx128m -jar target\api-gateway-1.0.0.jar"
timeout /t 5 /nobreak >nul

echo.
echo [7/7] Launching Frontend Web Server (Port 3000)...
start "Frontend-Server-3000" cmd /k "title Frontend Server (3000) && cd /d "%~dp0" && node local-server.js"
timeout /t 3 /nobreak >nul

echo.
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4 Address"') do (
    set "MY_IP=%%a"
    goto :ip_done
)
:ip_done
set "MY_IP=%MY_IP: =%"

echo ===================================================================
echo     ALL MICROSERVICES & FRONTEND ARE LIVE!
echo ===================================================================
echo   - Local Access:       http://localhost:3000
echo   - Mobile / LAN Access: http://%MY_IP%:3000
echo   - Eureka Dashboard:   http://localhost:8761
echo   - API Gateway:        http://localhost:8080
echo   - Auth Service:       http://localhost:8081
echo   - Booking Service:    http://localhost:8082
echo   - Record Service:     http://localhost:8083
echo   - Billing Service:    http://localhost:8084
echo ===================================================================
echo Opening PrecisionAuto Care in your browser...
start "" "http://localhost:3000"
echo.
echo * NEED A WORLDWIDE PUBLIC LINK FOR FACULTY TO TEST ANYWHERE?
echo   Run "START_PUBLIC_LINK.bat" to generate an instant HTTPS public link!
echo.
echo You can keep this window minimized.
