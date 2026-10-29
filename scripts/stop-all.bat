@echo off
echo Stopping all PrecisionAuto Care microservice processes...
taskkill /F /FI "WINDOWTITLE eq Eureka Server*" /T 2>nul
taskkill /F /FI "WINDOWTITLE eq Auth Service*" /T 2>nul
taskkill /F /FI "WINDOWTITLE eq Record Service*" /T 2>nul
taskkill /F /FI "WINDOWTITLE eq Billing Service*" /T 2>nul
taskkill /F /FI "WINDOWTITLE eq Booking Service*" /T 2>nul
taskkill /F /FI "WINDOWTITLE eq API Gateway*" /T 2>nul
echo All microservice processes stopped.
