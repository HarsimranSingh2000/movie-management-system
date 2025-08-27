@echo off
setlocal

echo ================================================
echo   MOVIE MANAGEMENT SYSTEM - STARTUP LAUNCHER
echo ================================================
echo.
echo Choose launch mode:
echo   1. Docker (Production-like)
echo   2. Local (Development)
echo.

set /p choice="Enter choice [1 or 2]: "

if "%choice%"=="1" goto run_docker
if "%choice%"=="2" goto run_local

echo Invalid choice. Exiting...
goto end

:run_docker
echo.
echo Starting Movie Management System with DOCKER profile...
docker-compose up --build
goto end

:run_local
echo.
echo Starting local PostgreSQL (dev-db) container...
docker start dev-db >nul 2>&1

echo.
echo Building Spring Boot application...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Maven build failed. Exiting...
    goto end
)

echo.
echo Running app with LOCAL profile...
java -Dspring.profiles.active=local -jar target\movie-management-system-0.0.1-SNAPSHOT.jar
goto end

:end
endlocal
