#!/bin/bash

set -ex

./ci-scripts/ci/tasks/create-maven-settings-xml.sh

cd code-repo
./mvnw clean verify -s ${HOME}/.m2/settings.xml
