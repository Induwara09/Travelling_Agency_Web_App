param([Parameter(Mandatory=$true)][string]$BackupFile)
if (!(Test-Path $BackupFile)) { throw "Backup file not found: $BackupFile" }
Write-Host "WARNING: This restores into the local smartpos database." -ForegroundColor Yellow
$confirm = Read-Host "Type RESTORE to continue"
if ($confirm -ne "RESTORE") { exit }
$env:MYSQL_PWD = $env:SMARTPOS_DB_PASSWORD
Get-Content $BackupFile | mysql -h localhost -u $env:SMARTPOS_DB_USER smartpos
Remove-Item Env:MYSQL_PWD
Write-Host "Restore command completed. Verify data before reopening cashier operations." -ForegroundColor Green
