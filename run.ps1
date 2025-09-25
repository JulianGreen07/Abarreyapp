# Run the AbarreyApp JAR if present
# Usage: Open PowerShell in this folder and run: .\run.ps1

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
# Support two layouts: wrapper folder (for_github\AbarreyApp\AbarreyApp) or direct project folder (for_github\AbarreyApp)
$possible1 = Join-Path $scriptRoot 'AbarreyApp\target\AbarreyApp-1.0-SNAPSHOT.jar'
$possible2 = Join-Path $scriptRoot 'target\AbarreyApp-1.0-SNAPSHOT.jar'

if (Test-Path $possible2) { $jarPath = $possible2 }
elseif (Test-Path $possible1) { $jarPath = $possible1 }
else {
    Write-Host "JAR no encontrado en ninguna ruta esperada:" -ForegroundColor Yellow
    Write-Host "  - $possible2" -ForegroundColor Yellow
    Write-Host "  - $possible1" -ForegroundColor Yellow
    Write-Host "Opciones:" -ForegroundColor Cyan
    Write-Host "  - Instalar Maven y ejecutar: mvn -f AbarreyApp/pom.xml clean package" -ForegroundColor White
    Write-Host "  - O compilar manualmente con javac y poner dependencias en AbarreyApp\lib" -ForegroundColor White
    exit 1
}

Write-Host "Arrancando: $jarPath" -ForegroundColor Green
# Ejecuta el jar sin consola si es posible usando javaw
$javaw = "${env:JAVA_HOME}\bin\javaw.exe"
if (-not (Test-Path $javaw)) { $javaw = "javaw.exe" }
if (Get-Command $javaw -ErrorAction SilentlyContinue) {
    Start-Process -FilePath $javaw -ArgumentList "-jar", "$jarPath" -WindowStyle Normal
} else {
    # fallback a java (puede abrir consola)
    Start-Process -FilePath java -ArgumentList "-jar", "$jarPath" -WindowStyle Normal
}
