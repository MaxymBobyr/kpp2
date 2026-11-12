@echo off
REM =====================================================================
REM  run.bat — Подвійний клік для запуску повної системи
REM  MySQL -> Tomcat -> JavaFX-клієнт
REM =====================================================================
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -NoProfile -File "%~dp0run.ps1"
if errorlevel 1 (
    echo.
    echo [X] Запуск завершився з помилкою. Перегляньте повідомлення вище.
    pause
)
