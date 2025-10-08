<#
  setup_db.ps1
  Script interactivo para crear la base de datos 'abarrey_db', crear un usuario de aplicación
  (por defecto 'abarrey_user') y ejecutar el script sql/init_db.sql incluido en la distribución.

  Uso: ejecutar desde la raíz del proyecto (donde está la carpeta sql). El script llamará al
  cliente `mysql` y pedirá la contraseña de root cuando sea necesario.

  Nota de seguridad: el script está pensado para entorno local/educativo. No almacena
  contraseñas en texto en el repositorio. Si prefieres automatizar sin prompts, edítalo con
  cuidado y evita subir credenciales.
#>

Param()

function Confirm-CommandPresent {
    param([string]$cmd)
    $which = Get-Command $cmd -ErrorAction SilentlyContinue
    return -not [object]::ReferenceEquals($which, $null)
}

if (-not (Confirm-CommandPresent -cmd 'mysql')) {
    Write-Host "No se encontró el cliente 'mysql' en PATH. Por favor instala MySQL client o ajusta tu PATH." -ForegroundColor Yellow
    Write-Host "Ejemplo de ruta: 'C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe'"
    Write-Host "Si ya tienes mysql, ejecuta este script desde una consola donde 'mysql' sea accesible."
    exit 1
}

Write-Host "== Preparación de la base de datos para AbarreyApp ==" -ForegroundColor Cyan

$rootUser = Read-Host "Usuario administrador de MySQL (por defecto 'root')"
if ([string]::IsNullOrWhiteSpace($rootUser)) { $rootUser = 'root' }

Write-Host "El script necesitará la contraseña del usuario '$rootUser' para ejecutar las operaciones." -ForegroundColor Yellow
Write-Host "Cuando se solicite, introduce la contraseña (se ocultará). Si tu cuenta root no tiene contraseña, simplemente pulsa Enter cuando se te pida." -ForegroundColor Yellow

Read-Host -AsSecureString "Pulsa Enter para continuar (se te pedirá la contraseña por `mysql -p`)" | Out-Null

# SQL de creación básica
$setupSql = @"
CREATE DATABASE IF NOT EXISTS abarrey_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'abarrey_user'@'localhost' IDENTIFIED BY 'ChangeMe123!';
GRANT ALL PRIVILEGES ON abarrey_db.* TO 'abarrey_user'@'localhost';
FLUSH PRIVILEGES;
"@

Write-Host "Creando base de datos y usuario (se abrirá el prompt de contraseña de MySQL)..." -ForegroundColor Green

try {
    # Ejecuta las sentencias (mysql pedirá la contraseña interactiva con -p)
    & mysql -u $rootUser -p -e $setupSql
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Advertencia: la creación pudo fallar (exit code $LASTEXITCODE). Revisa la salida anterior." -ForegroundColor Red
    } else {
        Write-Host "Base de datos y usuario creados/actualizados correctamente." -ForegroundColor Green
    }
} catch {
    Write-Host "Error al ejecutar mysql: $_" -ForegroundColor Red
    exit 1
}

Write-Host "Importando esquemas y datos de ejemplo desde sql/init_db.sql..." -ForegroundColor Green
if (-not (Test-Path -Path "./sql/init_db.sql")) {
    Write-Host "No se encontró './sql/init_db.sql' en la ruta actual. Asegúrate de ejecutar este script desde la raíz del proyecto." -ForegroundColor Red
    exit 1
}

try {
    # Importa el script en la base creada: evitamos la redirección '<' usando Get-Content y piping
    Get-Content -Path .\sql\init_db.sql -Raw | & mysql -u $rootUser -p abarrey_db
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Importación pudo fallar (exit code $LASTEXITCODE). Revisa la salida anterior." -ForegroundColor Red
    } else {
        Write-Host "Importación completada. La base 'abarrey_db' contiene ahora tablas y datos de ejemplo." -ForegroundColor Green
    }
} catch {
    Write-Host "Error durante la importación: $_" -ForegroundColor Red
    exit 1
}

Write-Host "Resumen: base 'abarrey_db' lista y usuario 'abarrey_user' con contraseña 'ChangeMe123!' creado (acceso solo desde localhost)." -ForegroundColor Cyan
Write-Host "Si quieres cambiar la contraseña o el usuario, edita 'src/main/resources/db.properties' y reconstruye la aplicación con ./build.ps1." -ForegroundColor Cyan

Write-Host "Fin. Ahora puedes arrancar la aplicación con scripts/run_app.ps1 o con la línea de comando Java." -ForegroundColor Cyan
