#!/bin/sh

version=`cat version/version`
artifactName="${ARTIFACT_ID}-${version}.jar"

cd deployment

export CALCULATED_GROUP_ID=${GROUP_ID//\./\/}
ARTIFACT_FULL_URI="${ARTIFACTORY_URL}/${GROUP_ID//\.//}/${ARTIFACT_ID}/${VERSION}/${ARTIFACT_NAME}"
echo "Downloading full artifact uri: ${ARTIFACT_FULL_URI}"
wget "${ARTIFACT_FULL_URI}"

cp ../code-repo/ci-manifest.yml manifest.yml

sed -i -- "s|path: .*$|path: $artifactName|g" manifest.yml
sed -i -- "s|- route: sampleroute|- route: ${ROUTE}|g" manifest.yml

cat manifest.yml
