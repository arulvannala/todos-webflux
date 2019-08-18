#!/bin/sh

export VERSION=`cat version/version`
echo "Build version: ${VERSION}"

echo "list of current directory contents"
ls -ltr
ls -ltr ci-scripts
ls -ltr ci-scripts/ci
ls -ltr ci-scripts/ci/tasks



echo "Calling script to generate settings"
cat ci-scripts/ci/tasks/generate-settings.sh

cd ci-scripts/ci/tasks

ls -ltr

./generate-settings.sh

[[ -z "${M2_HOME}" ]] && M2_HOME="${HOME}/.m2"
[[ -z "${M2_CACHE}" ]] && M2_CACHE="${ROOT_FOLDER}/maven"

echo "Generating symbolic links for caches"

[[ -d "${M2_CACHE}" && ! -d "${M2_HOME}" ]] && ln -s "${M2_CACHE}" "${M2_HOME}"

echo "Writing maven settings to [${M2_HOME}/settings.xml]"

cat > "${M2_HOME}/settings.xml" <<EOF

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


cd ../../../code-repo

echo "Running mvn deploy command"
./mvnw deploy \
    -DnewVersion=${VERSION} \
    -DskipTests \
    -Ddistribution.management.release.id="${M2_SETTINGS_REPO_ID}" \
    -Ddistribution.management.release.url="${REPO_WITH_BINARIES_FOR_UPLOAD}"
