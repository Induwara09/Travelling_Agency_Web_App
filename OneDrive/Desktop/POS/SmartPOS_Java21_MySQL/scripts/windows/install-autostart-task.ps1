$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$bat = "$root\scripts\windows\start-smartpos.bat"
$action = New-ScheduledTaskAction -Execute "cmd.exe" -Argument "/c `"$bat`""
$trigger = New-ScheduledTaskTrigger -AtLogOn
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable
Register-ScheduledTask -TaskName "SmartPOS Auto Start" -Action $action -Trigger $trigger -Settings $settings -Description "Start local SmartPOS after Windows cashier login" -Force
Write-Host "SmartPOS startup task installed." -ForegroundColor Green
