#! /bin/bash
echo "Starting File Transfer Service..."

java -jar --add-exports=java.base/jdk.internal.ref=ALL-UNNAMED -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:5005  /filetransfer.jar
