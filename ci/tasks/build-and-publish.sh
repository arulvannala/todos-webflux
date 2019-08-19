#!/bin/sh

export version=`cat version/version`
echo "Build version: ${version}"

cd code-repo

# Create custom settings.xml file with credentials required to publish to remote maven repi
cat > "settings.xml" <<EOF

<?xml version="1.0" encoding="UTF-8"?>
<settings>
	<servers>
		<server>
			<id>\${M2_SETTINGS_REPO_ID}</id>
			<username>\${M2_SETTINGS_REPO_USERNAME}</username>
			<password>\${M2_SETTINGS_REPO_PASSWORD}</password>
		</server>
	</servers>
</settings>


EOF
echo "Settings xml written"

# Update version and deploy to remote maven repository
echo "Running mvn deploy command"
./mvnw versions:set -DnewVersion=${version}
./mvnw deploy \
    -DskipTests \
    -Ddistribution.management.release.id="${M2_SETTINGS_REPO_ID}" \
    -Ddistribution.management.release.url="${REPO_WITH_BINARIES_FOR_UPLOAD}" \
    --settings settings.xml

# Create file with tag name to be used in later put step
echo "version-${version}-artifactory-deploy-$(date +%Y%m%d_%H%M%S)" > ../results/tag.txt