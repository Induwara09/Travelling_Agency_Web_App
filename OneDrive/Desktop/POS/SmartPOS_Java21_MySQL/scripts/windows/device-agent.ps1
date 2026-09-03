# SmartPOS lightweight Windows health heartbeat.
# For a remote monitoring server, change $Endpoint to its HTTPS URL.
$Endpoint = "http://localhost:8080/api/devices/heartbeat"
$AgentKey = if ($env:SMARTPOS_DEVICE_AGENT_KEY) { $env:SMARTPOS_DEVICE_AGENT_KEY } else { "CHANGE_ME_DEVICE_AGENT_KEY" }
$TerminalId = if ($env:COMPUTERNAME) { $env:COMPUTERNAME } else { "POS-01" }
while ($true) {
  try {
    $cpu = (Get-CimInstance Win32_Processor | Measure-Object -Property LoadPercentage -Average).Average
    $os = Get-CimInstance Win32_OperatingSystem
    $ram = [math]::Round((1 - ($os.FreePhysicalMemory / $os.TotalVisibleMemorySize)) * 100, 1)
    $drive = Get-CimInstance Win32_LogicalDisk -Filter "DeviceID='C:'"
    $disk = if ($drive.Size -gt 0) { [math]::Round((1 - ($drive.FreeSpace / $drive.Size)) * 100, 1) } else { 0 }
    $posRunning = (Test-NetConnection -ComputerName localhost -Port 8080 -WarningAction SilentlyContinue).TcpTestSucceeded
    $mysqlSvc = Get-Service | Where-Object { $_.Name -like 'MySQL*' } | Select-Object -First 1
    $dbRunning = $null -ne $mysqlSvc -and $mysqlSvc.Status -eq 'Running'
    $body = @{
      terminalId=$TerminalId; deviceName=$env:COMPUTERNAME; cpuPercent=$cpu; ramPercent=$ram; diskPercent=$disk;
      posRunning=$posRunning; databaseRunning=$dbRunning; windowsVersion=$os.Caption; appVersion='1.0.0'
    } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri $Endpoint -Headers @{"X-Agent-Key"=$AgentKey} -ContentType 'application/json' -Body $body | Out-Null
  } catch { Write-Host "Heartbeat failed: $($_.Exception.Message)" }
  Start-Sleep -Seconds 60
}
