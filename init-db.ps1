# init-db.ps1
# Ejecuta el script SQL de inicialización contra una instancia MySQL local.
# Uso: ./init-db.ps1 -User root -Password yourpassword
param(
    [string] $User = "root",
    [string] $Password = "",
    [string] $Host = "localhost",
    [int] $Port = 3306
)

$script = Join-Path $PSScriptRoot "sql\init_db.sql"
if (-not (Test-Path $script)) {
    Write-Error "No se encontró $script"
    exit 1
}

# Intenta ejecutar mysql si está disponible
$mysql = "mysql"
try {
    $proc = Start-Process -FilePath $mysql -ArgumentList "-u$User -p$Password -h $Host -P $Port" -NoNewWindow -PassThru -RedirectStandardInput $script -Wait -ErrorAction Stop
} catch {
    Write-Host "No se pudo ejecutar el cliente MySQL desde el PATH. Puedes ejecutar manualmente:\nmysql -u $User -p -h $Host -P $Port < $script"
}

Write-Host "Script de inicialización intentado: $script"