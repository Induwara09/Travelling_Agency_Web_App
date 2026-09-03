@echo off
setlocal
cd /d "%~dp0\..\.."
start "SmartPOS Backend" /min java -jar backend\target\smartpos-backend-1.0.0.jar
ping 127.0.0.1 -n 5 >nul
start "" http://localhost:8080
endlocal
