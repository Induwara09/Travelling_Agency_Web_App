$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
Write-Host "SmartPOS production build" -ForegroundColor Green
Write-Host "1/4 Building React frontend..."
Set-Location "$root\frontend"
npm install
npm run build
Write-Host "2/4 Copying frontend into Spring Boot static resources..."
$static = "$root\backend\src\main\resources\static"
if (Test-Path $static) { Remove-Item $static -Recurse -Force }
New-Item -ItemType Directory -Path $static | Out-Null
Copy-Item "$root\frontend\dist\*" $static -Recurse -Force
Write-Host "3/4 Building Spring Boot JAR with Java 21..."
Set-Location "$root\backend"
mvn clean package -DskipTests
Write-Host "4/4 Complete"
Write-Host "JAR: $root\backend\target\smartpos-backend-1.0.0.jar" -ForegroundColor Cyan
