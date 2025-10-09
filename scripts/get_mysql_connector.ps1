# Downloads MySQL Connector/J into lib\
$destDir = Join-Path (Get-Location) 'lib'
if (!(Test-Path $destDir)) { New-Item -ItemType Directory -Path $destDir | Out-Null }
# Try several recent versions and common artifact names
$versions = @('8.0.33','8.0.32','8.0.31','8.0.30','8.0.29','8.0.28')
$patterns = @(
    'mysql/mysql-connector-java/{0}/mysql-connector-java-{0}.jar',
    'mysql/mysql-connector-j/{0}/mysql-connector-j-{0}.jar',
    'com/mysql/mysql-connector-j/{0}/mysql-connector-j-{0}.jar'
)

$downloaded = $false
foreach ($version in $versions) {
    $jarName = "mysql-connector-j-$version.jar"
    $jarPath = Join-Path $destDir $jarName
    if (Test-Path $jarPath) { Write-Host "Connector already present: $jarPath"; $downloaded = $true; break }
    foreach ($p in $patterns) {
        $url = "https://repo1.maven.org/maven2/" + ($p -f $version)
        Write-Host "Trying $url"
        try {
            Invoke-WebRequest -Uri $url -OutFile $jarPath -UseBasicParsing -ErrorAction Stop
            Write-Host "Downloaded to $jarPath"
            $downloaded = $true
            break
        } catch {
            # continue
        }
    }
    if ($downloaded) { break }
}

if (-not $downloaded) {
    Write-Host "Could not download connector automatically. Please download the MySQL Connector/J JAR (e.g. mysql-connector-j-8.0.x.jar) from https://dev.mysql.com/downloads/connector/j/ and place it into the 'lib' folder of this project."
    exit 1
}
