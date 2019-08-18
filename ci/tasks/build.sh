#!/bin/sh

export VERSION=`cat version/version`
echo "Build version: ${VERSION}"

cd code-repo
./mvnw versions:set -DnewVersion=${VERSION}
./mvnw clean package

# Copy war file to concourse output folder
cd ..
cp code-repo/target/*.jar build-artifacts/
