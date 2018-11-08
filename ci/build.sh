#!/bin/sh

set -e -x

cd source-code
  ./mvnw clean package
cd ..

cp source-code/target/todos-webflux-1.0.0.SNAP.jar  build-output/.