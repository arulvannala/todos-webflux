#!/bin/bash

set -ex

export version=`cat version/version`
echo "Build version: ${version}"

repository=$(pwd)/distribution-repository

cd code-repo

# Update version and deploy to remote maven repository
echo "Running mvn deploy command"
./mvnw versions:set \
    -DnewVersion=${version}
./mvnw deploy \
    -DskipTests \
    -DaltDeploymentRepository=distribution::default::file://${repository}

# Create file with tag name to be used in later put step
echo "version-${version}-artifactory-deploy" > ../results/tag.txt