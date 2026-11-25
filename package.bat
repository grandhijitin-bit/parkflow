@echo off
setlocal
echo ===================================================
echo   Packaging Executable JAR...
echo ===================================================

call build.bat
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

set "JAVA_BIN=C:\Program Files\Java\jdk-26.0.2.1\bin"
if exist "%JAVA_BIN%\jar.exe" (
    set "PATH=%JAVA_BIN%;%PATH%"
)

if not exist "target" mkdir target

echo Main-Class: app.Main> manifest.txt
echo Class-Path: ../lib/sqlite-jdbc-3.46.1.0.jar ../lib/slf4j-api-1.7.36.jar lib/sqlite-jdbc-3.46.1.0.jar lib/slf4j-api-1.7.36.jar>> manifest.txt

jar cfm target\parking-system.jar manifest.txt -C target\classes .
del manifest.txt

if %ERRORLEVEL% EQU 0 (
    echo [OK] Executable JAR created at target\parking-system.jar
    echo You can run it with: java -jar target\parking-system.jar
) else (
    echo [ERROR] Packaging failed!
)
