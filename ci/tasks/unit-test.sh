#!/bin/bash

set -ex

cd code-repo
./mvnw clean test -s ${HOME}/.m2/settings.xml
