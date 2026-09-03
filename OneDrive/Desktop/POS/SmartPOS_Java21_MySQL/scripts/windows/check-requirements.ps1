$ErrorActionPreference = "Continue"
Write-Host "=========================================" -ForegroundColor Green
Write-Host " SmartPOS - Windows Requirements Checker" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green

function Check-Command($name, $versionArgs) {
    $cmd = Get-Command $name -ErrorAction SilentlyContinue
    if (-not $cmd) {
        Write-Host "[MISSING] $name" -ForegroundColor Red
        return $false
    }
    Write-Host "[OK] $name -> $($cmd.Source)" -ForegroundColor Green
    try { & $name $versionArgs 2>&1 | Select-Object -First 4 | ForEach-Object { Write-Host "     $_" } } catch {}
    return $true
}

$javaOk = Check-Command "java" "-version"
$mavenOk = Check-Command "mvn" "-version"
$nodeOk = Check-Command "node" "--version"
$npmOk = Check-Command "npm" "--version"
$mysqlOk = Check-Command "mysql" "--version"
$dumpOk = Check-Command "mysqldump" "--version"

if ($javaOk) {
    $javaText = (& java -version 2>&1 | Out-String)
    if ($javaText -match 'version "21\.0\.12') {
        Write-Host "[OK] Exact requested Java 21.0.12 detected." -ForegroundColor Green
    } elseif ($javaText -match 'version "21\.') {
        Write-Host "[INFO] Java 21 detected. Project target is Java 21; requested runtime is Java 21.0.12." -ForegroundColor Yellow
    } else {
        Write-Host "[WARNING] Java 21 was not detected. Install Java 21.0.12 LTS before building." -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "MySQL service check:" -ForegroundColor Cyan
Get-Service -ErrorAction SilentlyContinue | Where-Object { $_.Name -match 'MySQL' } | Format-Table Name, Status, StartType -AutoSize

Write-Host ""
if ($javaOk -and $mavenOk -and $nodeOk -and $npmOk -and $mysqlOk) {
    Write-Host "Core development requirements are available." -ForegroundColor Green
} else {
    Write-Host "Install the missing requirements before running SmartPOS." -ForegroundColor Yellow
}
