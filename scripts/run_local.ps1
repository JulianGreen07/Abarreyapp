# Compile and run the AbarreyApp project locally
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run_local.ps1
Set-Location -Path "$PSScriptRoot\.."

if (!(Test-Path 'target\classes')) {
    New-Item -ItemType Directory -Path 'target\classes' | Out-Null
}

# copy resources (including db.properties) into target/classes
Write-Host 'Copying resources to target/classes...'
Get-ChildItem -Recurse -Include '*.properties','*.png','*.jpg','*.xml','*.sql' | ForEach-Object {
    # build a relative path for destination under target/classes
    $root = (Get-Location).Path
    $rel = $_.FullName.Substring($root.Length)
    # remove leading directory separator if present
    if ($rel.StartsWith('\') -or $rel.StartsWith('/')) { $rel = $rel.Substring(1) }
    # skip files already under target/classes to avoid nested copying
    if ($rel -match '^target(\\|/)classes') { return }
    $dest = Join-Path 'target/classes' $rel
    $dpath = Split-Path $dest -Parent
    if (!(Test-Path $dpath)) { New-Item -ItemType Directory -Path $dpath -Force | Out-Null }
    # avoid copying a file onto itself
    if ($_.FullName -ne (Resolve-Path $dest -ErrorAction SilentlyContinue)) {
        Copy-Item -Force -Path $_.FullName -Destination $dest
    }
}

# build list of jars inside lib/
$libJars = @()
if (Test-Path 'lib') {
    $libJars = Get-ChildItem -Path 'lib' -Filter '*.jar' | ForEach-Object { $_.FullName }
}

$sources = Get-ChildItem -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
if ($sources.Count -eq 0) {
    Write-Host 'No .java sources found'
    exit 1
}

$javacArgs = @('-d','target/classes')
if ($libJars.Count -gt 0) {
    $cpArg = $libJars -join ';'
    $javacArgs += @('-cp', $cpArg)
}
$javacArgs += $sources

Write-Host 'Compiling sources...'
& javac @javacArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "javac failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}

$runCp = 'target/classes'
if ($libJars.Count -gt 0) { $runCp = "$runCp;" + ($libJars -join ';') }

Write-Host "Launching app with classpath: $runCp"
& java -cp $runCp com.abarreyapp.Main
