#!/bin/bash
# requires bash avoce for the variable substitution used below

set -ex

version=`cat version/version`
artifactName="${ARTIFACT_ID}-${version}.jar"


cd code-repo
cp ci-manifest.yml ../deployment/manifest.yml
cd ..

cd deployment

# Calculate the proper artifactory URI
echo "GROUP_ID=${GROUP_ID}"
echo "ARTIFACT_ID=${ARTIFACT_ID}"
export CALCULATED_GROUP_ID=${GROUP_ID//\./\/}
ARTIFACT_FULL_URI="${ARTIFACTORY_URL}/${GROUP_ID//\.//}/${ARTIFACT_ID}/${version}/${artifactName}"

# Get jar file from artifactory and put in deployment directory
echo "Downloading full artifact uri: ${ARTIFACT_FULL_URI}"
wget "${ARTIFACT_FULL_URI}"

# Update the path and route for versioned jar and requested cf route
sed -i -- "s|path: .*$|path: $artifactName|g" manifest.yml
sed -i -- "s|- route: sampleroute|- route: ${ROUTE}|g" manifest.yml

cat manifest.yml

# Create github tag file to be used in later put step
echo "${version}-cf-${DEPLOY_TARGET_LABEL}-deploy-$(date +%Y%m%d_%H%M%S)" > tag.txt