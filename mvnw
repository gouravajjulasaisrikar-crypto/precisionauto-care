#!/usr/bin/env bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/usr/lib/jvm/java-21-openjdk" ]; then
        export JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
    fi
fi
if [ -f "$SCRIPT_DIR/.tools/apache-maven-3.9.9/bin/mvn" ]; then
    exec "$SCRIPT_DIR/.tools/apache-maven-3.9.9/bin/mvn" "$@"
else
    exec mvn "$@"
fi
