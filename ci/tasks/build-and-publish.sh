#!/bin/sh

export version=`cat version/version`
echo "Build version: ${version}"

cd code-repo

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

echo "Running mvn deploy command"
./mvnw versions:set -DnewVersion=${version}
./mvnw deploy \
    -DskipTests \
    -Ddistribution.management.release.id="${M2_SETTINGS_REPO_ID}" \
    -Ddistribution.management.release.url="${REPO_WITH_BINARIES_FOR_UPLOAD}" \
    --settings settings.xml

echo "version-${version}-artifactory-deploy-$(date +%Y%m%d_%H%M%S)" > ../results/tag.txt