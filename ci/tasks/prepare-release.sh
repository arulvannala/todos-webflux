#!/bin/sh

export version=`cat version/version`
echo "Build version: ${version}"

echo "version-${version}-release" > release-results/release-name.txt

rm -rf code-repo/.git
tar -cvf release-results/code-repo-${version}.tgz code-repo
