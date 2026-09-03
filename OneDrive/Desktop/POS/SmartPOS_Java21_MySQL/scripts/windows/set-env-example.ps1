# Run PowerShell as Administrator if using machine-wide variables.
# Change all passwords/secrets before production deployment.
[Environment]::SetEnvironmentVariable("SMARTPOS_DB_URL", "jdbc:mysql://localhost:3306/smartpos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Colombo", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_DB_USER", "smartpos", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_DB_PASSWORD", "SmartPos@2026", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_JWT_SECRET", "REPLACE_WITH_A_RANDOM_SECRET_AT_LEAST_32_BYTES_LONG", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_BUSINESS_NAME", "Mountain Cafe", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_BUSINESS_ADDRESS", "21 Lake Road, Nuwara Eliya", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_BUSINESS_PHONE", "052-0000000", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_BUSINESS_EMAIL", "owner@example.com", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_ADMIN_PASSWORD", "CHANGE_ADMIN_PASSWORD", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_MANAGER_PASSWORD", "CHANGE_MANAGER_PASSWORD", "User")
[Environment]::SetEnvironmentVariable("SMARTPOS_CASHIER_PASSWORD", "CHANGE_CASHIER_PASSWORD", "User")

# Optional SMTP settings for invoice/report email
# [Environment]::SetEnvironmentVariable("SMARTPOS_MAIL_HOST", "smtp.example.com", "User")
# [Environment]::SetEnvironmentVariable("SMARTPOS_MAIL_PORT", "587", "User")
# [Environment]::SetEnvironmentVariable("SMARTPOS_MAIL_USER", "pos@example.com", "User")
# [Environment]::SetEnvironmentVariable("SMARTPOS_MAIL_PASSWORD", "APP_PASSWORD", "User")
# [Environment]::SetEnvironmentVariable("SMARTPOS_MAIL_AUTH", "true", "User")
# [Environment]::SetEnvironmentVariable("SMARTPOS_MAIL_STARTTLS", "true", "User")
# [Environment]::SetEnvironmentVariable("SMARTPOS_REPORT_RECIPIENTS", "owner@example.com,accountant@example.com", "User")
