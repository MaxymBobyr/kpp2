# =====================================================================
#  run.ps1 — Запуск повної системи: MySQL → Tomcat → JavaFX-клієнт
#
#  Використання:
#    powershell -ExecutionPolicy Bypass -File .\run.ps1
#  або подвійний клік по run.bat
#
#  Скрипт:
#    1) перевіряє/запускає MySQL;
#    2) перевіряє/запускає Apache Tomcat 9.x;
#    3) чекає, поки Tomcat підніметься і розгорне WAR;
#    4) перевіряє, що серверний JSON-маршрут відповідає;
#    5) запускає JavaFX-клієнт;
#    6) після закриття клієнта зупиняє Tomcat (за потреби).
# =====================================================================

$ErrorActionPreference = "Stop"

# --- Кодування консолі (щоб кирилиця відображалась коректно) -------
try { [Console]::OutputEncoding = [System.Text.Encoding]::UTF8 } catch {}
try { chcp 65001 | Out-Null } catch {}
$OutputEncoding = [System.Text.Encoding]::UTF8
# ------------------------------------------------------------------

# ------------------------- НАЛАШТУВАННЯ -------------------------
$TomcatHome = "C:\apache-tomcat-9.0.123"
$JavaHome   = "D:\Java21"
$MySqlHome  = "D:\xampp"
$ProjectRoot = $PSScriptRoot
$ServerUrl  = "http://localhost:8080/server/equipment"
$TomcatPort = 8080
$TomcatStartupTimeoutSec = 120
$MySqlStartupTimeoutSec  = 60
# ---------------------------------------------------------------

function Write-Step([string]$text) {
    Write-Host ""
    Write-Host "==> $text" -ForegroundColor Cyan
}

function Write-Ok([string]$text) {
    Write-Host "    [OK] $text" -ForegroundColor Green
}

function Write-Warn([string]$text) {
    Write-Host "    [!]  $text" -ForegroundColor Yellow
}

function Write-Err([string]$text) {
    Write-Host "    [X]  $text" -ForegroundColor Red
}

# --- Перевірка, чи слухає порт ------------------------------------
function Test-Port([int]$Port) {
    try {
        $conn = New-Object System.Net.Sockets.TcpClient
        $conn.Connect("127.0.0.1", $Port)
        $conn.Close()
        return $true
    } catch {
        return $false
    }
}

# --- Перевірка, чи відповідає URL --------------------------------
function Test-HttpOk([string]$Url) {
    try {
        $resp = Invoke-WebRequest -Uri $Url -Method Get -UseBasicParsing -TimeoutSec 10
        return ($resp.StatusCode -eq 200 -and $resp.Content.TrimStart().StartsWith("["))
    } catch {
        return $false
    }
}

# =====================================================================
# 1. MYSQL
# =====================================================================
Write-Step "Перевірка MySQL"

if (Test-Port 3306) {
    Write-Ok "MySQL вже працює (порт 3306)"
} else {
    $mysqld = Join-Path $MySqlHome "mysql\bin\mysqld.exe"
    $myini  = Join-Path $MySqlHome "mysql\bin\my.ini"

    if (-not (Test-Path $mysqld)) {
        Write-Err "Не знайдено mysqld.exe: $mysqld"
        Write-Err "Відредагуйте змінну `$MySqlHome у файлі run.ps1"
        exit 1
    }

    Write-Warn "Запускаю MySQL..."
    if (Test-Path $myini) {
        Start-Process -FilePath $mysqld -ArgumentList "--defaults-file=$myini" -WindowStyle Hidden
    } else {
        Start-Process -FilePath $mysqld -WindowStyle Hidden
    }

    $deadline = (Get-Date).AddSeconds($MySqlStartupTimeoutSec)
    while ((Get-Date) -lt $deadline) {
        Start-Sleep -Seconds 1
        if (Test-Port 3306) {
            Write-Ok "MySQL запущено"
            break
        }
    }
    if (-not (Test-Port 3306)) {
        Write-Err "MySQL не запустився за $MySqlStartupTimeoutSec секунд"
        exit 1
    }
}

# =====================================================================
# 2. TOMCAT
# =====================================================================
Write-Step "Перевірка Apache Tomcat"

$env:JAVA_HOME = $JavaHome
$catalina = Join-Path $TomcatHome "bin\catalina.bat"

if (-not (Test-Path $catalina)) {
    Write-Err "Не знайдено catalina.bat: $catalina"
    Write-Err "Відредагуйте змінну `$TomcatHome у файлі run.ps1"
    exit 1
}

$startedTomcat = $false

if (Test-Port $TomcatPort) {
    Write-Ok "Tomcat вже працює (порт $TomcatPort)"
} else {
    Write-Warn "Запускаю Tomcat..."
    Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/k", "cd /d `"$TomcatHome\bin`" && catalina.bat run" `
        -WindowStyle Normal
    $startedTomcat = $true
}

# --- Чекаємо, поки порт відкриється -------------------------------
Write-Warn "Очікую готовності Tomcat (до $TomcatStartupTimeoutSec с)..."
$deadline = (Get-Date).AddSeconds($TomcatStartupTimeoutSec)
$portUp = $false
while ((Get-Date) -lt $deadline) {
    if (Test-Port $TomcatPort) {
        $portUp = $true
        break
    }
    Start-Sleep -Seconds 1
}

if (-not $portUp) {
    Write-Err "Tomcat не почав слухати порт $TomcatPort за $TomcatStartupTimeoutSec секунд"
    exit 1
}
Write-Ok "Tomcat слухає порт $TomcatPort"

# --- Чекаємо, поки WAR розгорнеться і JSON-маршрут відповідає ------
Write-Warn "Очікую розгортання WAR і готовності JSON-маршруту..."
$deadline = (Get-Date).AddSeconds($TomcatStartupTimeoutSec)
$appReady = $false
while ((Get-Date) -lt $deadline) {
    if (Test-HttpOk $ServerUrl) {
        $appReady = $true
        break
    }
    Start-Sleep -Seconds 2
}

if (-not $appReady) {
    Write-Err "Серверний маршрут $ServerUrl не повертає JSON"
    Write-Err "Перевірте логи Tomcat: $TomcatHome\logs\catalina.*.log"
    exit 1
}
Write-Ok "Серверний маршрут готовий і повертає JSON"

# =====================================================================
# 3. JAVAFX-КЛІЄНТ
# =====================================================================
Write-Step "Запуск JavaFX-клієнта"

Push-Location (Join-Path $ProjectRoot "client")

try {
    & mvn javafx:run
} finally {
    Pop-Location
}

# =====================================================================
# 4. ЗУПИНКА TOMCAT (якщо його запускав цей скрипт)
# =====================================================================
if ($startedTomcat) {
    Write-Step "Зупинка Tomcat"
    Get-Process -Name "java" -ErrorAction SilentlyContinue |
        Where-Object { $_.Path -like "$TomcatHome*" } |
        ForEach-Object { Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue }
    Write-Ok "Tomcat зупинено"
}

Write-Step "Готово"
