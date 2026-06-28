@echo off
setlocal
set "ROOT=%~dp0"
cd /d "%ROOT%"

if "%DB_SERVER%"=="" set "DB_SERVER=localhost"
if "%DB_PORT%"=="" set "DB_PORT=3306"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$server=$env:DB_SERVER; $port=[int]$env:DB_PORT;" ^
  "try { $client=New-Object System.Net.Sockets.TcpClient; $iar=$client.BeginConnect($server,$port,$null,$null); if (-not $iar.AsyncWaitHandle.WaitOne(3000,$false)) { throw 'Timeout while connecting to MySQL' }; $client.EndConnect($iar); $client.Close(); Write-Host 'MySQL/XAMPP port is reachable:' $server':'$port; exit 0 } catch { Write-Host 'MySQL/XAMPP connection FAILED'; Write-Host $_.Exception.Message; exit 1 }"
