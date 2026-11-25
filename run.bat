@echo off
setlocal
echo ===================================================
echo   Launching Vehicle Parking Management System...
echo ===================================================

set "JAVA_BIN=C:\Program Files\Java\jdk-26.0.2.1\bin"
if exist "%JAVA_BIN%\java.exe" (
    set "PATH=%JAVA_BIN%;%PATH%"
)

java -cp "lib\*;target\classes" app.Main
