@echo off
title PrecisionAuto Care - Frontend Web Server
echo Starting PrecisionAuto Care Web Server on http://localhost:3000 ...
start "Frontend-Server-3000" cmd /k "title Frontend Server (3000) && cd /d "%~dp0frontend" && node server.js"
timeout /t 2 /nobreak >nul
echo Opening PrecisionAuto Care on http://localhost:3000 ...
start "" "http://localhost:3000"
exit
