<#
.SYNOPSIS
Stops local HireConnect backend services started by run-local-backend.ps1.

.DESCRIPTION
This script stops the PowerShell/Java process trees recorded in run-local-backend.pids.json.
It does not stop Windows infrastructure services like MySQL or RabbitMQ.
Redis and Elasticsearch are running inside WSL in this setup, so they are stopped only when -StopWsl is provided.

.EXAMPLE
./stop-local-backend.ps1

.EXAMPLE
./stop-local-backend.ps1 -StopWsl
#>

[CmdletBinding()]
param(
    [switch]$Force,
    [switch]$StopWsl
)

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$pidFile = Join-Path $root 'run-local-backend.pids.json'

function Write-Info($message) {
    Write-Host "[INFO] $message" -ForegroundColor Cyan
}

function Write-Ok($message) {
    Write-Host "[OK] $message" -ForegroundColor Green
}

function Write-Warn($message) {
    Write-Host "[WARN] $message" -ForegroundColor Yellow
}

function Stop-ProcessTreeById {
    param(
        [Parameter(Mandatory = $true)]
        [int]$ProcessId
    )

    try {
        $proc = Get-Process -Id $ProcessId -ErrorAction SilentlyContinue

        if ($null -eq $proc) {
            Write-Warn "Process with PID $ProcessId is not running."
            return
        }

        Write-Info "Stopping process tree for PID $ProcessId..."

        & taskkill.exe /PID $ProcessId /T /F | Out-Null

        Start-Sleep -Seconds 1

        $stillRunning = Get-Process -Id $ProcessId -ErrorAction SilentlyContinue
        if ($null -eq $stillRunning) {
            Write-Ok "Stopped process tree for PID $ProcessId."
        } else {
            Write-Warn "PID $ProcessId may still be running."
        }
    } catch {
        Write-Warn "Failed to stop PID ${ProcessId}: ${_}"
    }
}

function Stop-WslServices {
    Write-Warn "Stopping WSL. This will stop Redis and Elasticsearch running inside WSL."

    try {
        wsl --shutdown
        Write-Ok "WSL stopped successfully."
    } catch {
        Write-Warn "Failed to stop WSL: ${_}"
    }
}

Write-Host ""
Write-Host "Stopping HireConnect local backend..." -ForegroundColor Magenta
Write-Host ""

if (Test-Path $pidFile) {
    try {
        $state = Get-Content $pidFile -Raw | ConvertFrom-Json

        if ($null -ne $state.Services) {
            foreach ($entry in $state.Services) {
                if ($null -ne $entry.Pid) {
                    Stop-ProcessTreeById -ProcessId ([int]$entry.Pid)
                }
            }
        } else {
            Write-Warn "No service process entries found in PID file."
        }

        Remove-Item $pidFile -Force -ErrorAction SilentlyContinue
        Write-Ok "Removed PID file."
    } catch {
        Write-Warn "Could not read or process PID file: ${_}"
        Write-Warn "You may need to close service windows manually."
    }
} else {
    Write-Warn "PID file not found: $pidFile"
    Write-Warn "Maybe services were started manually or the PID file was already deleted."
}

if ($StopWsl) {
    Stop-WslServices
} else {
    Write-Warn "WSL was not stopped. Use -StopWsl to stop Redis and Elasticsearch."
}

Write-Host ""
Write-Host "Local backend stop complete." -ForegroundColor Green
Write-Host ""
Write-Host "Common usage:" -ForegroundColor Cyan
Write-Host "  ./stop-local-backend.ps1"
Write-Host "  ./stop-local-backend.ps1 -StopWsl"
Write-Host ""