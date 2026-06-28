@echo off
setlocal
set "ROOT=%~dp0"
set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.3\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%ROOT%"
if "%DB_USERNAME%"=="" set /p DB_USERNAME=MySQL username [root]: 
if "%DB_USERNAME%"=="" set "DB_USERNAME=root"
if "%DB_PASSWORD%"=="" set /p DB_PASSWORD=MySQL password [empty for XAMPP]: 
if "%DB_SERVER%"=="" set "DB_SERVER=localhost:3306"
if "%DB_NAME%"=="" set "DB_NAME=plateforme_hse"
if "%DB_URL%"=="" set "DB_URL=jdbc:mysql://%DB_SERVER%/%DB_NAME%?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

echo Starting application. Full log: run.log
call mvnw.cmd spring-boot:run > run.log 2>&1
if errorlevel 1 (
  echo.
  echo Application failed. Important errors:
  echo ------------------------------------------------------------
  findstr /i /c:"Caused by:" /c:"ERROR" /c:"Login failed" /c:"Connection refused" /c:"database" /c:"Address already in use" run.log
  echo ------------------------------------------------------------
  echo Full details are in:
  echo "%ROOT%run.log"
  echo.
  pause
  exit /b 1
)
