@echo off
title FinEdge Platform Stopper
color 0C
echo ======================================================================
echo                  Stopping FinEdge Services
echo ======================================================================
echo.

echo Terminating services on ports 8001, 8080, and 3000...

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8001" ^| findstr "LISTENING"') do (
    echo Stopping FastAPI process [PID %%a]...
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo Stopping Spring Boot process [PID %%a]...
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3000" ^| findstr "LISTENING"') do (
    echo Stopping Vite Frontend process [PID %%a]...
    taskkill /F /PID %%a >nul 2>&1
)

echo.
echo All FinEdge services stopped.
echo ======================================================================
pause
