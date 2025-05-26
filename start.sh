#!/bin/bash

# Set Java path
export JAVA_HOME=/opt/render/.jdk
export PATH=$JAVA_HOME/bin:$PATH

# Debugging logs
echo "--- DEBUG INFO ---"
echo "JAVA_HOME: $JAVA_HOME"
echo "PATH: $PATH"
$JAVA_HOME/bin/java -version || echo "❌ Java not found!"
echo "JAR files in target/:"
ls -l target/

# Run the JAR (confirm exact name below matches target/)
JAR_FILE="FSDProject-0.0.1-SNAPSHOT.jar"
if [ -f "target/$JAR_FILE" ]; then
    echo "✅ Found JAR. Starting application..."
    $JAVA_HOME/bin/java -jar target/$JAR_FILE
else
    echo "❌ JAR file not found: target/$JAR_FILE"
    exit 1
fi
