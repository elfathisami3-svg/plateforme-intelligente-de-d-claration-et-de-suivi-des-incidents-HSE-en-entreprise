@echo off
setlocal
set "ROOT=%~dp0"
set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%ROOT%"
call mvnw.cmd -q "-DskipTests" package
