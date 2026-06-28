@echo off
setlocal
set "ROOT=%~dp0"
set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%ROOT%"

if "%DB_USERNAME%"=="" set /p DB_USERNAME=MySQL username [root]: 
if "%DB_USERNAME%"=="" set "DB_USERNAME=root"
if "%DB_PASSWORD%"=="" set /p DB_PASSWORD=MySQL password [empty for XAMPP]: 
if "%DB_SERVER%"=="" set "DB_SERVER=localhost:3306"
if "%DB_NAME%"=="" set "DB_NAME=plateforme_hse"
if "%DB_URL%"=="" set "DB_URL=jdbc:mysql://%DB_SERVER%/%DB_NAME%?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

echo Writing full startup log to run-debug.log
call mvnw.cmd spring-boot:run -e > run-debug.log 2>&1
echo.
echo Application stopped. Open run-debug.log and search for "Caused by:" or "ERROR".
echo.
pause
