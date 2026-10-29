@echo off
title PrecisionAuto Care - Public Access Link Generator
color 0B
echo ===================================================================
echo     PRECISIONAUTO CARE - PUBLIC ACCESS LINK ENGINE
echo ===================================================================
echo.
echo Checking if frontend server is running on port 3000...
powershell -NoProfile -Command "(Get-NetTCPConnection -LocalPort 3000 -State Listen -ErrorAction SilentlyContinue)" >nul 2>&1
if %errorlevel% neq 0 (
    echo Starting Frontend Web Server & API Gateway Reverse Proxy...
    start "Frontend-Server-3000" cmd /k "title Frontend Server (3000) && cd /d "%~dp0" && node local-server.js"
    timeout /t 2 /nobreak >nul
)

echo Starting Cloudflare Edge Tunnel...
node "%~dp0scripts\start-tunnel.js"
pause
