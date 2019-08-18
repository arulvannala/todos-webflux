#!/bin/sh

export VERSION=`cat version/version`
echo "Build version: ${VERSION}"

echo "list of current directory contents"
ls -ltr
ls -ltr ci-scripts
ls -ltr ci-scripts/ci
ls -ltr ci-scripts/ci/tasks



echo "Calling script to generate settings"
cat ci-scripts/ci/tasks/generate-settings.sh

cd ci-scripts/ci/tasks

./generate-settings.sh

cd ../../../code-repo

echo "Running mvn deploy command"
./mvnw deploy \
    -DnewVersion=${VERSION} \
    -DskipTests \
    -Ddistribution.management.release.id="${M2_SETTINGS_REPO_ID}" \
    -Ddistribution.management.release.url="${REPO_WITH_BINARIES_FOR_UPLOAD}"
