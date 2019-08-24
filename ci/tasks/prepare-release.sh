#!/bin/sh

set -ex

export version=`cat version/version`
echo "Build version: ${version}"

# Create file with release name to be used in later put step
echo "version-${version}-release" > release-results/release-name.txt

# Create github release artifact consisting of zipped up code from repo
rm -rf code-repo/.git
tar -cvf release-results/code-repo-${version}.tgz code-repo
