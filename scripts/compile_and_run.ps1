# Compile and run AbarreyApp from src
# Change directory to the project root (one level up from scripts folder)
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location -Path (Join-Path $scriptDir '..')
# Ensure target/classes exists
$classesDir = Join-Path -Path (Get-Location) -ChildPath "target\classes"
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null
# Collect .java files
$srcRoot = Join-Path -Path (Get-Location) -ChildPath "src\main\java"
$files = Get-ChildItem -Recurse -Filter '*.java' -Path $srcRoot | ForEach-Object { $_.FullName }
Write-Host "Found files:" $files.Count
if ($files.Count -eq 0) {
    Write-Error 'No .java files found under src\main\java'
    exit 1
}
# Compile
javac -d $classesDir -cp "lib/*;target/lib/*" $files
if ($LASTEXITCODE -ne 0) {
    Write-Error 'javac failed'
    exit $LASTEXITCODE
}
Write-Host 'Compilation OK'
# Run main class
java -cp "lib/*;target/lib/*;target/classes" com.abarreyapp.Main
