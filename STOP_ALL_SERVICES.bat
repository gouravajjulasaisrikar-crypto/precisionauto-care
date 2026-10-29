@echo off
title PrecisionAuto Care - Stop All Services
color 0C
echo ===================================================================
echo     PRECISIONAUTO CARE - SHUTDOWN ALL MICROSERVICES & SERVERS
echo ===================================================================
echo.
echo Terminating running microservice JARs, Gateway, Eureka, and Node...
taskkill /F /FI "WINDOWTITLE eq Eureka Server*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Auth Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Record Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Billing Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Booking Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq API Gateway*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Frontend Server*" 2>nul
taskkill /F /IM cloudflared.exe 2>nul

echo Releasing ports 8761, 8080, 8081, 8082, 8083, 8084, 3000...
for %%p in (8761 8080 8081 8082 8083 8084 3000) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":%%p " ^| findstr "LISTENING"') do (
        taskkill /F /PID %%a 2>nul
    )
)

echo.
echo [SUCCESS] All microservices and frontend servers have been stopped.
echo Ports 8761, 8080, 8081, 8082, 8083, 8084, 3000 are clean.
timeout /t 3
exit
