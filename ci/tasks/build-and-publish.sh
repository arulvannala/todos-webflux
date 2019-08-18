#!/bin/sh

export VERSION=`cat version/version`
echo "Build version: ${VERSION}"

cd code-repo

echo "Calling script to generate settings"
./ci/tasks/maven/create-maven-settings-xml.sh

echo "Running mvn deploy command"
./mvnw deploy \
    -DnewVersion=${VERSION} \
    -DskipTests \
    -Ddistribution.management.release.id="${M2_SETTINGS_REPO_ID}" \
    -Ddistribution.management.release.url="${REPO_WITH_BINARIES_FOR_UPLOAD}"
