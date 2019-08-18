#!/bin/sh

export VERSION=`cat version/version`
echo "Build version: ${VERSION}"

echo "Calling script to generate settings"
./ci-scripts/ci/tasks/create-maven-settings-xml.sh

cd code-repo

echo "Running mvn deploy command"
./mvnw deploy \
    -DnewVersion=${VERSION} \
    -DskipTests \
    -Ddistribution.management.release.id="${M2_SETTINGS_REPO_ID}" \
    -Ddistribution.management.release.url="${REPO_WITH_BINARIES_FOR_UPLOAD}"
