#!/bin/sh

export version=`cat version/version`
echo "Build version: ${version}"

tar -cvf results/code-repo-${version}.tgz code-repo
echo "version-${version}-release" > results/release-name.txt
