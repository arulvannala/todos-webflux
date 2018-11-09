#!/bin/sh
export MAVEN_OPTS="-Xms2g -Xmx2g"
export VERSION=`cat source-code/version`
echo "Building version $VERSION"
cd source-code
  ./mvnw clean package -Dversion=${VERSION} -Dmaven.test.skip=true
cd ..

cp source-code/target/todos-webflux-${VERSION}.jar  build-output/.