@echo off
setlocal
cd /d "%~dp0"

start "Employee360 Backend" cmd /k "cd /d ""%~dp0"" && ""%~dp0mvnw.cmd"" spring-boot:run"
start "Employee360 Frontend" cmd /k "cd /d ""%~dp0frontend"" && npm run dev"

powershell -NoProfile -ExecutionPolicy Bypass -Command "$deadline = (Get-Date).AddMinutes(2); do { $backend = Test-NetConnection -ComputerName 127.0.0.1 -Port 8080 -InformationLevel Quiet; $frontend = Test-NetConnection -ComputerName 127.0.0.1 -Port 5173 -InformationLevel Quiet; if ($backend -and $frontend) { Start-Process 'http://localhost:5173'; exit 0 }; Start-Sleep -Seconds 2 } while ((Get-Date) -lt $deadline); Write-Error 'Employee360 did not become ready. Check the backend and frontend windows.'; pause; exit 1"

endlocal
