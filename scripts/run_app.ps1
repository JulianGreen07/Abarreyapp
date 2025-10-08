<#
  run_app.ps1
  Ejecuta la aplicación empaquetada (target/AbarreyApp-1.0-SNAPSHOT.jar) con todas las
  dependencias en la carpeta lib/ añadidas al classpath. Ejecuta en primer plano para mostrar
  la salida de la JVM y facilitar la depuración.

  Uso:
    Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force
    ./scripts/run_app.ps1
#>

Set-Location -Path (Split-Path -Parent $MyInvocation.MyCommand.Definition)
Set-Location -Path '..'

$jar = Join-Path -Path (Get-Location) -ChildPath 'target\AbarreyApp-1.0-SNAPSHOT.jar'
if (-not (Test-Path $jar)) {
    Write-Host "No se encontró el JAR en 'target'. Ejecuta ./build.ps1 primero." -ForegroundColor Red
    exit 1
}

Write-Host "Ejecutando la aplicación con todas las librerías en lib/ (se mostrará la salida en la consola)..." -ForegroundColor Cyan

$libPattern = Join-Path -Path (Get-Location) -ChildPath 'lib\*'

# Construir classpath Windows: las entradas separadas por ';' y el wildcard 'lib/*' resuelve con la JVM
$classpath = "$libPattern;$jar"

Write-Host "Classpath: $classpath" -ForegroundColor DarkGray

java -cp $classpath com.abarreyapp.Main
