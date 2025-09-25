<#
build.ps1 - Compila y empaqueta AbarreyApp sin Maven
Uso: Ejecuta desde la raíz del repo:
  .\build.ps1
Que hace:
  - Comprueba `javac` y `jar` están disponibles
  - Crea `AbarreyApp\lib` y descarga `flatlaf-3.4.1.jar` si no existe
  - Compila todos los .java bajo `AbarreyApp\src\main\java` a `AbarreyApp\target\classes`
  - Crea `manifest.txt` con Main-Class y Class-Path
  - Empaqueta `AbarreyApp\target\AbarreyApp-1.0-SNAPSHOT.jar`
#>

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Si este script está colocado directamente dentro del proyecto (tiene src), usar scriptRoot como raíz del proyecto.
# De lo contrario, buscar una carpeta 'AbarreyApp' anidada (compatibilidad para diseño de wrapper).
if (Test-Path (Join-Path $scriptRoot 'src')) {
    $projectRoot = $scriptRoot
} elseif (Test-Path (Join-Path $scriptRoot 'AbarreyApp')) {
    $projectRoot = Join-Path $scriptRoot 'AbarreyApp'
} else {
    # retroceder a scriptRoot (mejor esfuerzo)
    $projectRoot = $scriptRoot
}

$src = Join-Path $projectRoot 'src\main\java'
$res = Join-Path $projectRoot 'src\main\resources'
$lib = Join-Path $projectRoot 'lib'
$target = Join-Path $projectRoot 'target'

if (-not (Test-Path $lib)) { New-Item -ItemType Directory -Path $lib | Out-Null }
if (-not (Test-Path $target)) { New-Item -ItemType Directory -Path $target | Out-Null }

# Asegurarse de que flatlaf jar esté presente
$flatlaf = Join-Path $lib 'flatlaf-3.4.1.jar'
if (-not (Test-Path $flatlaf)) {
    Write-Host "flatlaf no encontrado en $flatlaf" -ForegroundColor Yellow
    Write-Host "Por favor copie flatlaf-3.4.1.jar en $lib y vuelva a ejecutar." -ForegroundColor Cyan
    exit 1
} else {
    Write-Host "flatlaf ya existe: $flatlaf" -ForegroundColor Green
}

# Encontrar compilador de Java
$javac = "${env:JAVA_HOME}\bin\javac.exe"
if (-not (Test-Path $javac)) { $javac = "javac.exe" }

if (-not (Get-Command $javac -ErrorAction SilentlyContinue)) {
    Write-Host "No se encontró javac. Asegúrate de tener JDK instalado y JAVA_HOME configurado." -ForegroundColor Red
    exit 1
}

# Recopilar archivos fuente
Write-Host "Compilando fuentes desde: $src"
$srcFiles = Get-ChildItem -Path $src -Recurse -Include *.java | ForEach-Object { $_.FullName }
$classesOut = Join-Path $target 'classes'
if (Test-Path $classesOut) { Remove-Item -Recurse -Force $classesOut }
New-Item -ItemType Directory -Path $classesOut | Out-Null

# Construir classpath para compilación
$cp = "$flatlaf"
$cpArg = $cp -join ';'

# Compilar (usar archivo @sources si hay muchos; javac soporta @argfiles pero la concatenación de PowerShell está bien)
& $javac -d "$classesOut" -classpath $cpArg @($srcFiles)
if ($LASTEXITCODE -ne 0) {
    Write-Host "javac falló." -ForegroundColor Red
    exit 1
}

# Copiar recursos
if (Test-Path $res) {
    Write-Host "Copiando recursos a: $classesOut"
    Copy-Item -Path (Join-Path $res '*') -Destination $classesOut -Recurse -Force
}

# Crear manifest
$manifest = Join-Path $target 'manifest.txt'
"Main-Class: com.abarreyapp.Main" | Out-File -FilePath $manifest -Encoding ascii

# Empaquetar JAR
$jar = Join-Path $target 'AbarreyApp-1.0-SNAPSHOT.jar'
if (Test-Path $jar) { Remove-Item $jar }

$jarExe = "${env:JAVA_HOME}\bin\jar.exe"
if (-not (Test-Path $jarExe)) { $jarExe = "jar.exe" }

# Construir el jar usando la herramienta jar
& $jarExe cfm "$jar" "$manifest" -C "$classesOut" .
if ($LASTEXITCODE -ne 0) {
    Write-Host "jar falló." -ForegroundColor Red
    exit 1
}

Write-Host "JAR creado: $jar" -ForegroundColor Green
Write-Host "Puedes ejecutar: java -jar $jar"
exit 0
