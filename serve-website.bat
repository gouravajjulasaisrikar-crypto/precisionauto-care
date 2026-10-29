@echo off
echo Starting local web server on http://localhost:3000 ...
start "" http://localhost:3000
node "%~dp0frontend\server.js"
