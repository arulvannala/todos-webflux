#!/bin/bash

set -ex

export version=`cat version/version`
echo "Build version: ${version}"

./ci-scripts/ci/tasks/create-maven-settings-xml.sh

export ROOT_FOLDER=$( pwd )
export BUILD_ID=`cat ${ROOT_FOLDER}/meta/build-id`
export BUILD_NAME=`cat ${ROOT_FOLDER}/meta/build-name`
export BUILD_TEAM_NAME=`cat ${ROOT_FOLDER}/meta/build-team-name`
export BUILD_PIPELINE_NAME=`cat ${ROOT_FOLDER}/meta/build-pipeline-name`
export BUILD_JOB_NAME=`cat ${ROOT_FOLDER}/meta/build-job-name`
export ATC_EXTERNAL_URL=`cat ${ROOT_FOLDER}/meta/atc-external-url`
export BUILD_URI=${ATC_EXTERNAL_URL}/teams/${BUILD_TEAM_NAME}/pipelines/${BUILD_PIPELINE_NAME}/jobs/${BUILD_PIPELINE_NAME}/build/${BUILD_NAME}

repository=$(pwd)/distribution-repository

cd code-repo

# Update version and deploy to remote maven repository
echo "Running mvn deploy command"
./mvnw versions:set \
    -DnewVersion=${version} \
    -s ${HOME}/.m2/settings.xml
./mvnw deploy \
    -DskipTests \
    -s ${HOME}/.m2/settings.xml \
    -DaltDeploymentRepository=distribution::default::file://${repository}

# Create file with tag name to be used in later put step
echo "version-${version}-artifactory-deploy" > ../results/tag.txt