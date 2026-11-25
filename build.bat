@echo off
setlocal
echo ===================================================
echo   Compiling Vehicle Parking Management System...
echo ===================================================

if not exist "target\classes" mkdir target\classes

set "JAVA_BIN=C:\Program Files\Java\jdk-26.0.2.1\bin"
if exist "%JAVA_BIN%\javac.exe" (
    set "PATH=%JAVA_BIN%;%PATH%"
)

powershell -NoProfile -ExecutionPolicy Bypass -Command "& { (Get-ChildItem -Path 'src\main\java' -Filter '*.java' -Recurse | ForEach-Object { (Resolve-Path -Relative $_.FullName).Replace('\', '/') }) | Out-File -Encoding ascii 'sources.txt'; & javac -cp 'lib\*' -d target/classes '@sources.txt'; Remove-Item 'sources.txt' }"

if %ERRORLEVEL% EQU 0 (
    echo [OK] Compilation successful! Classes compiled to target\classes
) else (
    echo [ERROR] Compilation failed!
    exit /b %ERRORLEVEL%
)
