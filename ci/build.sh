#!/bin/sh
export MAVEN_OPTS="-Xms2g -Xmx2g"
cd source-code
  ./mvnw clean package -Dmaven.test.skip=true
cd ..

cp source-code/target/todos-webflux-1.0.0.SNAP.jar  build-output/.