@echo off
title FinEdge Platform Launcher
color 0A
echo ======================================================================
echo       Starting FinEdge: Indian Portfolio & Risk Analytics Platform
echo ======================================================================
echo.

cd /d "%~dp0"

echo [1/3] Starting Quantitative Analytics Engine (Python FastAPI - Port 8001)...
start "FinEdge - FastAPI [Port 8001]" cmd /k ".venv\Scripts\python.exe scripts\run_fastapi.py"

echo [2/3] Starting Enterprise REST API Backend (Spring Boot - Port 8080)...
start "FinEdge - Spring Boot [Port 8080]" cmd /k "java -Dspring.profiles.active=dev -jar backend\target\portfolio-analytics-backend-1.0.0.jar"

echo [3/3] Starting Web Terminal Frontend (Vite React - Port 3000)...
start "FinEdge - Frontend [Port 3000]" cmd /k "cd frontend && npm run dev"

echo.
echo Waiting 5 seconds for services to initialize...
timeout /t 5 /nobreak > nul

echo.
echo Opening browser to http://localhost:3000 ...
start http://localhost:3000

echo.
echo ======================================================================
echo       All Services Running Successfully!
echo ======================================================================
echo   Frontend Terminal:   http://localhost:3000
echo   Spring Boot REST:    http://localhost:8080/api
echo   Swagger OpenAPI:     http://localhost:8080/swagger-ui/index.html
echo   FastAPI Engine:      http://localhost:8001/docs
echo.
echo   Default Login:       demo@portfolio.com
echo   Default Password:    password123
echo.
echo   To stop all services, run stop.bat or close the 3 server windows.
echo ======================================================================
pause
