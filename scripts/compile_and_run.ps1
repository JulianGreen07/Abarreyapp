<#
Compila y ejecuta AbarreyApp sin Maven (modo desarrollo rápido)
Equivalente a:
  cd AbarreyApp; $files = Get-ChildItem -Recurse -Filter *.java | % { $_.FullName }; 
  javac -cp "lib/*;target/classes" -d target/classes $files; 
  if ($LASTEXITCODE -eq 0) { java -cp "lib/*;target/classes" com.abarreyapp.Main } else { Write-Error 'Compilation failed' }
#>

# Cambiar a la raíz del módulo (un nivel arriba de scripts)
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location -Path (Join-Path $scriptDir '..')

# Asegurar carpeta de clases
$classesDir = Join-Path -Path (Get-Location) -ChildPath "target\classes"
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

# Copiar recursos (properties, imágenes, sql, etc.) a target/classes
Write-Host 'Copiando recursos a target/classes...'
$resRoot = Join-Path (Get-Location) 'src\main\resources'
if (Test-Path $resRoot) {
    Copy-Item -Path (Join-Path $resRoot '*') -Destination $classesDir -Recurse -Force
}

# Recolectar fuentes .java
$srcRoot = Join-Path -Path (Get-Location) -ChildPath "src\main\java"
$files = Get-ChildItem -Recurse -Filter '*.java' -Path $srcRoot | ForEach-Object { $_.FullName }
Write-Host "Fuentes encontradas:" $files.Count
if ($files.Count -eq 0) {
    Write-Error 'No se encontraron archivos .java bajo src\main\java'
    exit 1
}

# Compilar (classpath: jars en lib + clases previas)
$cp = 'lib/*;target/classes'
Write-Host 'Compilando...'
javac -cp $cp -d $classesDir $files
if ($LASTEXITCODE -ne 0) {
    Write-Error 'Compilation failed'
    exit $LASTEXITCODE
}
Write-Host 'Compilación OK'

# Ejecutar
Write-Host 'Lanzando aplicación...'
java -cp $cp com.abarreyapp.Main
