#!/usr/bin/env bash
set -e
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
  mkdir -p "$BASE_DIR/.mvn/wrapper"
  echo "Downloading maven wrapper jar..."
  if command -v curl >/dev/null 2>&1; then
    curl -sSL -o "$WRAPPER_JAR" "https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$WRAPPER_JAR" "https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"
  else
    echo "curl or wget required to download maven wrapper jar"
    exit 1
  fi
fi
exec java -jar "$WRAPPER_JAR" "$@"
