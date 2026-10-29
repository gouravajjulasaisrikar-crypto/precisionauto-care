@echo off
title PrecisionAuto Care - Public Access Link Generator
color 0B
echo ===================================================================
echo     PRECISIONAUTO CARE - PUBLIC ACCESS LINK ENGINE
echo ===================================================================
echo.
cd /d "%~dp0"
node scripts\start-tunnel.js
pause
