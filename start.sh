#!/bin/bash

# Set Java environment
export JAVA_HOME=/opt/render/.jdk
export PATH=$JAVA_HOME/bin:$PATH

# Debug info
echo "=== ENVIRONMENT ==="
env | sort
echo "=== JAVA VERSION ==="
java -version
echo "=== JAR FILES ==="
ls -l target/

# Run with explicit binding
JAR_FILE="target/FSDProject-0.0.1-SNAPSHOT.jar"
if [ -f "$JAR_FILE" ]; then
    echo "✅ Starting application..."
    java -jar "$JAR_FILE" --server.address=0.0.0.0 --server.port=$PORT
else
    echo "❌ Error: JAR file not found!"
    ls -l target/
    exit 1
fi