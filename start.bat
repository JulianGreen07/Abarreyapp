@echo off
REM Start the AbarreyApp GUI without opening a persistent console.
SETLOCAL
SET JAR=%~dp0target\AbarreyApp-1.0-SNAPSHOT.jar
IF NOT EXIST "%JAR%" (
  echo JAR not found at %JAR%
  echo Run build.ps1 first or open a PowerShell and run: powershell -NoProfile -ExecutionPolicy Bypass -File build.ps1
  pause
  exit /b 1
)
REM Try to use javaw to avoid console window
where javaw >nul 2>nul
IF %ERRORLEVEL%==0 (
  start "" javaw -jar "%JAR%"
) ELSE (
  start "" java -jar "%JAR%"
)
ENDLOCAL
exit /b 0