$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend'; mvn spring-boot:run"
Start-Sleep -Seconds 4
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\frontend'; npm run dev"
Start-Sleep -Seconds 3
Start-Process "http://localhost:5173"
