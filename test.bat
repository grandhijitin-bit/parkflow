@echo off
setlocal
echo ===================================================
echo   Running Automated Test Suite...
echo ===================================================

if not exist "target\test-classes" mkdir target\test-classes

set "JAVA_BIN=C:\Program Files\Java\jdk-26.0.2.1\bin"
if exist "%JAVA_BIN%\javac.exe" (
    set "PATH=%JAVA_BIN%;%PATH%"
)

powershell -NoProfile -ExecutionPolicy Bypass -Command "& { (Get-ChildItem -Path 'src\test\java' -Filter '*.java' -Recurse | ForEach-Object { (Resolve-Path -Relative $_.FullName).Replace('\', '/') }) | Out-File -Encoding ascii 'test-sources.txt'; & javac -cp 'lib\*;target/classes' -d target/test-classes '@test-sources.txt'; Remove-Item 'test-sources.txt' }"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Test compilation failed!
    exit /b %ERRORLEVEL%
)

java -cp "lib\*;target\classes;target\test-classes" TestParkingSystem
