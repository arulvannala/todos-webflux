#!/bin/sh

export VERSION=`cat version/version`
echo "Build version: ${VERSION}"

export M2_SETTINGS_REPO_ID=central
export REPO_WITH_BINARIES_FOR_UPLOAD=http://artifactory.kingslanding.pks.lab.winterfell.live/artifactory/libs-release-local
export REPO_WITH_BINARIES=http://artifactory.kingslanding.pks.lab.winterfell.live/artifactory/libs-release-local
export M2_SETTINGS_REPO_USERNAME=cody
export M2_SETTINGS_REPO_PASSWORD=AP3PYuBFwwSSE7DRWFZFBYASoHX

./ci/tasks/maven/create-maven-settings-xml.sh

cd code-repo
./mvnw deploy \
    -DnewVersion=${VERSION} \
    -DskipTests \
    -Ddistribution.management.release.id="${M2_SETTINGS_REPO_ID}" \
    -Ddistribution.management.release.url="${REPO_WITH_BINARIES_FOR_UPLOAD}"
