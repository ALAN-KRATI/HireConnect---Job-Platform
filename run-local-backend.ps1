<#
.SYNOPSIS
Starts the HireConnect backend services locally without Docker.

.DESCRIPTION
This script launches each Spring Boot microservice using its Maven wrapper.

It assumes the following infrastructure services are already running locally:
- MySQL on localhost:3306
- RabbitMQ on localhost:5672
- Redis on localhost:6379
- Elasticsearch on localhost:9200, if analytics/search needs it

This fixed version starts services sequentially:
1. Start one service in a new PowerShell window
2. Wait for its port to become available
3. Then start the next service

This prevents all JVMs from starting at the same time and reduces memory crashes.

.PARAMETER Services
Optional list of service folders to start. If omitted, it starts the full backend stack.

.PARAMETER NoWeb
Skip starting the `hireconnect-web` service.

.PARAMETER SkipDependencyCheck
Skip local dependency port checks for MySQL, RabbitMQ, Redis, and Elasticsearch.

.EXAMPLE
./run-local-backend.ps1

.EXAMPLE
./run-local-backend.ps1 -Services auth-service,job-service

.EXAMPLE
./run-local-backend.ps1 -NoWeb

.EXAMPLE
./run-local-backend.ps1 -SkipDependencyCheck
#>

[CmdletBinding()]
param(
    [string[]]$Services = @(
        'discover-server',
        'auth-service',
        'profile-service',
        'job-service',
        'notification-service',
        'application-service',
        'interview-microservice',
        'subscription-service',
        'analytics-service',
        'api-gateway',
        'hireconnect-web'
    ),
    [switch]$NoWeb,
    [switch]$SkipDependencyCheck
)

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$pidFile = Join-Path $root 'run-local-backend.pids.json'
$startedProcesses = @()
$startedInfrastructure = @()

function Write-Info($message) {
    Write-Host "[INFO] $message" -ForegroundColor Cyan
}

function Write-Warn($message) {
    Write-Host "[WARN] $message" -ForegroundColor Yellow
}

function Write-ErrorAndExit($message) {
    Write-Host "[ERROR] $message" -ForegroundColor Red
    exit 1
}

function Load-DotEnv {
    $envFile = Join-Path $root '.env'

    if (-not (Test-Path $envFile)) {
        Write-Warn ".env file not found at $envFile"
        Write-Warn "Continuing without .env. Make sure DB/Rabbit/Redis env variables are already set if needed."
        return
    }

    Write-Info "Loading environment variables from $envFile"

    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^[\s#]*$' -or $_ -match '^\s*#') {
            return
        }

        $parts = $_ -split '=', 2

        if ($parts.Length -ne 2) {
            return
        }

        $name = $parts[0].Trim()
        $value = $parts[1].Trim()

        if ([string]::IsNullOrWhiteSpace($name)) {
            return
        }

        # Remove optional wrapping quotes
        if (
            ($value.StartsWith('"') -and $value.EndsWith('"')) -or
            ($value.StartsWith("'") -and $value.EndsWith("'"))
        ) {
            $value = $value.Substring(1, $value.Length - 2)
        }

        Set-Item -Path "env:$name" -Value $value
    }

    if (-not $env:SPRING_DATASOURCE_PASSWORD -and $env:DB_PASSWORD) {
        Set-Item -Path 'env:SPRING_DATASOURCE_PASSWORD' -Value $env:DB_PASSWORD
    }

    if (-not $env:SPRING_DATASOURCE_USERNAME) {
        Set-Item -Path 'env:SPRING_DATASOURCE_USERNAME' -Value 'root'
    }
}

function Check-CommandExists($command) {
    $cmd = Get-Command $command -ErrorAction SilentlyContinue
    return $null -ne $cmd
}

function Verify-Prerequisites {
    if (-not (Check-CommandExists 'java')) {
        Write-ErrorAndExit 'Java is not installed or not on PATH. Install JDK 21+ and make sure `java` is available.'
    }

    if (-not (Check-CommandExists 'curl') -and -not (Check-CommandExists 'wget')) {
        Write-Warn 'Neither curl nor wget was found. HTTP checks will be disabled if needed.'
    }

    $wrapper = Join-Path $root 'auth-service\mvnw.cmd'

    if (-not (Test-Path $wrapper)) {
        Write-ErrorAndExit 'Maven wrapper not found in auth-service. Ensure this repository is intact.'
    }
}

function Test-Port($hostName, $portNumber, $serviceName) {
    try {
        $result = Test-NetConnection -ComputerName $hostName -Port $portNumber -WarningAction SilentlyContinue

        if ($result.TcpTestSucceeded) {
            Write-Host "[OK] $serviceName is reachable at ${hostName}:${portNumber}" -ForegroundColor Green
            return $true
        }

        Write-Warn "${serviceName} is not reachable at ${hostName}:${portNumber}"
        return $false
    } catch {
        Write-Warn "Could not test ${serviceName} on ${hostName}:${portNumber}: $_"
        return $false
    }
}

function Run-MySqlQuery($sql) {
    $mysqlCmd = Get-Command mysql -ErrorAction SilentlyContinue

    if ($null -eq $mysqlCmd) {
        $defaultMysqlPath = 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'

        if (Test-Path $defaultMysqlPath) {
            $mysqlExe = $defaultMysqlPath
        } else {
            Write-Warn 'MySQL CLI not found on PATH. Automatic database creation is disabled.'
            Write-Warn 'Either add MySQL bin folder to PATH or create databases manually.'
            return $false
        }
    } else {
        $mysqlExe = $mysqlCmd.Source
    }

    $dbUser = $env:SPRING_DATASOURCE_USERNAME

    if ([string]::IsNullOrWhiteSpace($dbUser)) {
        $dbUser = 'root'
    }

    $dbPassword = $env:SPRING_DATASOURCE_PASSWORD

    if ([string]::IsNullOrWhiteSpace($dbPassword)) {
        $dbPassword = $env:DB_PASSWORD
    }

    if ([string]::IsNullOrWhiteSpace($dbPassword)) {
        Write-Warn 'No DB password found in environment.'
        Write-Warn 'Set SPRING_DATASOURCE_PASSWORD or DB_PASSWORD in .env.'
        return $false
    }

    $arguments = @("-u$dbUser", "-p$dbPassword", '-e', $sql)

    try {
        & $mysqlExe @arguments

        if ($LASTEXITCODE -ne 0) {
            Write-Warn "MySQL command failed with exit code $LASTEXITCODE"
            return $false
        }

        return $true
    } catch {
        Write-Warn "Failed to run MySQL command: $_"
        return $false
    }
}

function Ensure-RequiredMySqlDatabases {
    $databases = @(
        'hireconnectdb',
        'hireconnect_auth',
        'hireconnect_profile',
        'hireconnect_interview',
        'hireconnect_applications',
        'hireconnect_notifications',
        'hireconnect_analytics',
        'hireconnect_subscription'
    )

    $sqlStatements = @()

    foreach ($db in $databases) {
        $sqlStatements += "CREATE DATABASE IF NOT EXISTS $db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    }

    $sqlStatements += 'FLUSH PRIVILEGES;'

    Write-Info 'Ensuring required MySQL databases exist...'

    if (Run-MySqlQuery ($sqlStatements -join ' ')) {
        Write-Info 'Required MySQL databases are created or already exist.'
    } else {
        Write-Warn 'Unable to automatically create required databases.'
        Write-Warn 'Create these manually if needed:'
        Write-Warn 'hireconnectdb, hireconnect_auth, hireconnect_profile, hireconnect_interview, hireconnect_applications, hireconnect_notifications, hireconnect_analytics, hireconnect_subscription'
    }
}

function Start-LocalWindowsService($serviceNames, $displayName) {
    foreach ($name in $serviceNames) {
        $svc = Get-Service -Name $name -ErrorAction SilentlyContinue

        if ($null -ne $svc) {
            if ($svc.Status -eq 'Running') {
                Write-Info "$displayName service '$name' is already running."
                return $true
            }

            try {
                Start-Service -Name $name
                Write-Info "Started $displayName service '$name'."
                $script:startedInfrastructure += $name
                return $true
            } catch {
                Write-Warn "Failed to start $displayName service '$name'. $_"
                return $false
            }
        }
    }

    Write-Warn "Could not find $displayName service on this machine. Install it or start it manually."
    return $false
}

function Start-LocalElasticsearch {
    $serviceNames = @(
        'elasticsearch',
        'Elasticsearch',
        'elasticsearch-service-x64',
        'elasticsearch-service-x86'
    )

    if (Start-LocalWindowsService $serviceNames 'Elasticsearch') {
        return $true
    }

    Write-Warn 'Elasticsearch service not found or failed to start.'
    Write-Warn 'If analytics/search does not need Elasticsearch right now, you can ignore this warning.'
    return $false
}

function Get-ServicePort($serviceName) {
    $ports = @{
        'discover-server'        = 8761
        'api-gateway'            = 8080
        'auth-service'           = 8081
        'profile-service'        = 8082
        'job-service'            = 8083
        'application-service'    = 8084
        'notification-service'   = 8086
        'interview-microservice' = 8085
        'subscription-service'   = 8087
        'analytics-service'      = 8088
        'hireconnect-web'        = 8090
    }

    if ($ports.ContainsKey($serviceName)) {
        return $ports[$serviceName]
    }

    return $null
}

function Wait-ForServicePort($serviceName, $port, $timeoutSeconds = 180) {
    if ($null -eq $port) {
        Write-Warn "No port configured for $serviceName. Waiting 20 seconds before starting next service."
        Start-Sleep -Seconds 20
        return $true
    }

    Write-Info "Waiting for $serviceName to become available on localhost:$port..."

    $elapsed = 0

    while ($elapsed -lt $timeoutSeconds) {
        try {
            $connection = Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue

            if ($connection.TcpTestSucceeded) {
                Write-Host "[OK] $serviceName is running on port $port" -ForegroundColor Green
                return $true
            }
        } catch {
            # ignore and retry
        }

        Start-Sleep -Seconds 5
        $elapsed += 5
    }

    Write-Warn "$serviceName did not become available on port $port within $timeoutSeconds seconds."
    Write-Warn "Continuing to next service anyway. Check the $serviceName terminal for errors."
    return $false
}

function Start-ServiceWindow($serviceName) {
    $serviceDir = Join-Path $root $serviceName

    if (-not (Test-Path $serviceDir)) {
        Write-Warn "Service directory not found: $serviceName"
        return $null
    }

    $wrapper = Join-Path $serviceDir 'mvnw.cmd'

    if (-not (Test-Path $wrapper)) {
        Write-Warn "Maven wrapper not found for service: $serviceName"
        return $null
    }

    $javaMemoryOptions = '-Xms96m -Xmx384m -XX:MaxMetaspaceSize=160m -XX:+UseSerialGC'

    $command = @"
Set-Location '$serviceDir'

`$env:MAVEN_OPTS='$javaMemoryOptions'

`$env:EUREKA_INSTANCE_PREFER_IP_ADDRESS='true'
`$env:EUREKA_INSTANCE_HOSTNAME='127.0.0.1'
`$env:EUREKA_INSTANCE_IP_ADDRESS='127.0.0.1'
`$env:JOB_SERVICE_URL='http://localhost:8083'

Write-Host '[INFO] Starting $serviceName with MAVEN_OPTS=' `$env:MAVEN_OPTS -ForegroundColor Cyan
Write-Host '[INFO] Eureka hostname=' `$env:EUREKA_INSTANCE_HOSTNAME ', ip=' `$env:EUREKA_INSTANCE_IP_ADDRESS ', prefer-ip=' `$env:EUREKA_INSTANCE_PREFER_IP_ADDRESS -ForegroundColor Cyan

.\mvnw.cmd -DskipTests spring-boot:run
"@

    $processArgs = @(
        '-NoExit',
        '-Command',
        $command
    )

    Write-Info "Starting $serviceName in a new PowerShell window..."

    return Start-Process powershell `
        -ArgumentList $processArgs `
        -WorkingDirectory $serviceDir `
        -WindowStyle Normal `
        -PassThru
}

function Resolve-ServiceList {
    param([string[]]$requested)

    $normalized = $requested | ForEach-Object {
        $_.Trim()
    } | Where-Object {
        $_ -ne ''
    }

    if ($normalized.Count -eq 0) {
        return @()
    }

    return $normalized
}

# ---------------------------
# Main
# ---------------------------

Set-Location $root

Load-DotEnv

$env:EUREKA_INSTANCE_PREFER_IP_ADDRESS = "true"
$env:EUREKA_INSTANCE_HOSTNAME = "localhost"
$env:EUREKA_INSTANCE_IP_ADDRESS = "127.0.0.1"

Verify-Prerequisites

if (Test-Path $pidFile) {
    Remove-Item $pidFile -Force -ErrorAction SilentlyContinue
}

$serviceList = Resolve-ServiceList -requested $Services

if ($NoWeb) {
    $serviceList = $serviceList | Where-Object {
        $_ -ne 'hireconnect-web'
    }
}

if ($serviceList.Count -eq 0) {
    Write-ErrorAndExit 'No services specified to start.'
}

$defaultDependencyPorts = @(
    @{Host='localhost'; Port=3306; Name='MySQL'},
    @{Host='localhost'; Port=5672; Name='RabbitMQ'},
    @{Host='localhost'; Port=6379; Name='Redis'},
    @{Host='localhost'; Port=9200; Name='Elasticsearch'}
)

if (-not $SkipDependencyCheck) {
    Write-Info 'Checking local dependency ports...'

    foreach ($dep in $defaultDependencyPorts) {
        Test-Port $dep.Host $dep.Port $dep.Name | Out-Null
    }

    Write-Host ''
}

Start-LocalWindowsService @('MySQL80','MySQL57','MySQL') 'MySQL'
Ensure-RequiredMySqlDatabases
Start-LocalElasticsearch
Start-LocalWindowsService @('RabbitMQ','RabbitMQServer','rabbitmq') 'RabbitMQ'
Start-LocalWindowsService @('Redis','redis') 'Redis'

Write-Host ''
Write-Info 'Services to start sequentially:'
$serviceList | ForEach-Object {
    Write-Host " - $_"
}
Write-Host ''

foreach ($service in $serviceList) {
    $proc = Start-ServiceWindow $service

    if ($proc -ne $null) {
        $startedProcesses += [pscustomobject]@{
            Service = $service
            Pid = $proc.Id
        }

        $port = Get-ServicePort $service
        Wait-ForServicePort $service $port 180 | Out-Null
    } else {
        Write-Warn "Skipping wait because $service did not start."
    }

    Write-Host ''
}

if ($startedProcesses.Count -gt 0) {
    $startedState = [pscustomobject]@{
        Services = $startedProcesses
        Infrastructure = $startedInfrastructure
    }

    $startedState | ConvertTo-Json -Depth 5 | Out-File $pidFile -Encoding utf8
}

Write-Host ''
Write-Host 'Backend startup launched sequentially.' -ForegroundColor Green
Write-Host 'Each service was started one by one, with port checks between services.'
Write-Host 'Open the console windows above to inspect logs and service startup progress.'
Write-Host 'If you are running the frontend separately, point it to http://localhost:8080.'
Write-Host ''
Write-Host 'Recommended service startup order used:'
Write-Host '  discover-server -> auth-service -> profile-service -> job-service -> notification-service -> application-service -> interview-microservice -> subscription-service -> analytics-service -> api-gateway -> hireconnect-web'
Write-Host ''
Write-Host 'If a service fails to start, verify local MySQL, RabbitMQ, Redis, Elasticsearch, and Eureka connectivity.'
Write-Host ''
Write-Host 'Examples:'
Write-Host '  ./run-local-backend.ps1'
Write-Host '  ./run-local-backend.ps1 -NoWeb'
Write-Host '  ./run-local-backend.ps1 -Services discover-server,auth-service,api-gateway'
Write-Host '  ./run-local-backend.ps1 -SkipDependencyCheck'