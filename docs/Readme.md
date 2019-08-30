# Example App CI/CD Pipeline

The contents of this folder represent a sample application CI/CD pipeline implemented 
with [Concourse](https://concourse-ci.org/).

Application CI/CD pipelines can be modeled in many ways.  And Concourse it's is a very flexible 
tool offering simple abstractions to create your pipelines.  The pipeline presented here is just one
approach that hits of several key topics.

## Example Pipeline

![Example Pipeline](example-pipeline.png)

General Requirements

- Source is maintained in Github
- Packaged java artifacts to be deployed to Artifactory
- Github repo tags and releases are leveraged
- Semantic versioning is used
    - Suffix of -rc.X is used for release candidates
    - Java artifacts have versions applied to artifact version
    - Git repo is tagged with each build applying version
    - After verification in test environment, release decision is made
        - Git repo release and tag created reference version
        - Java artifacts updated with release version and deployed to artifactory
- Ability to bump minor and major release numbers
- Includes deployment to test and prod Pivotal Application Services spaces

### Jobs Explained

- `unit-test` - Triggers of any code commit to the repo.  Simply runs `mvn verify` to ensure unit tests pass
- `build-and-publish` - Automatically triggered via success of previous job. Retrieves the version with the next rc number.  Creates custom settings.xml file with 
credentials to do a maven deploy.  Updates the java projects artifact version and then runs `mvn deploy` (without tests
 they were run the previous job) to deploy artifacts to local repository.  Then leverages artifactory-resource to publish
 artifacts to artifactory.  Pushes a new git tag with version number and
 timestamp as well as updates the version resource
- `deploy-to-test` - Automatically triggered via success of previous job.  Retrieves the .jar file from Artifactory, updates the cloud foundry manifest with artifact name
and desired route, then pushes app to PAS.  Additionally creates git tag on the repo indicating that the version was
deployed to test with a timestamp.
- `mark-as-release` - Manually triggered when developer indicates testing has been completed and release version should
be created.  Updates boot app's maven version and performs `mvn deploy` to publish locally and artifactory-resource to publish
to Artifactory with proper maven version.  Creates new git tag indicating new version.  Zips up the code-repo contents and creates a github
release with the version number and timestamp indicated.
- `deploy-to-prod` - Manually triggered.  Like `deploy-to-test` stage, this Retrieves the .jar from Artifactory, 
updates the cloud foundry manifest with artifact name and desired route, then pushes app to PAS.  Additionally 
creates git tag on the repo indicating that the version was deployed to prod with a timestamp. 
- `deploy-release-test` - Manually triggered.  Like `deploy-to-test` stage.  Gives ability to update test environment
with the actual jar file that was deployed to prod.
- `bump-patch-version` - Automatically triggered.  This sets up the next version of the app after release was cut.
Note that there is a small side-affect that -rc.1 will never be used in any version as next commit will bump to -rc.2
- `bump-major-version`, `bump-minor-version` - Manually triggered bump major and minor versions.

### What more could be done

- Automated smoke tests
- Blue-green deployments
- Deployment rollbacks
- Movement of CI scripts to its own repo for reuse
- Managing separate cf manifests
- Notifications to email or slack
- ...
- [Cloud Pipelines] (https://github.com/CloudPipelines/) introduces several of these concepts and more
   
### Concourse Resources Used

The used came out of the box with concourse

- git
- github-release
- semver
- cf

I've added the following 3rd party resources

- artifactory-resource - provided by the spring team

Info on these resources and more can be found on the [Concourse Duty Free](https://concourse.github.io/dutyfree/) site.

### Custom Tasks

I choose to use the cloudpipelines/pipeline-base docker image to run my tasks.  It was setup nicely to be
able to intercept the container for troubleshooting and had all the required tools  This is maintained by the Cloud
Pipelines project mentioned above.

### What you need to work this example

- Github: Fork this repo on github: https://github.com/doddatpivotal/todos-webflux
- [Github Deploy Key](https://developer.github.com/v3/guides/managing-deploy-keys/#deploy-keys) - Updating git repo
- [Github Access Key](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line) for creating github release
- Artifactory: I deployed CE edition to k8s using helm: https://www.jfrog.com/confluence/display/RTF/Installing+on+Kubernetes
- Pivotal Cloud Foundry: I used PCFOne which is internal to Pivots, but you could use [Pivotal Web Services](https://run.pivotal.io/) or any other
- Concourse: Here is instructions from my homelab setup: https://github.com/doddatpivotal/homelab-concourse-setup

### Example .secrets.yml file

```yaml
m2-settings-repo-username: username
m2-settings-repo-password: SuperSecretPassword
repo-context-url: http://artifactory.kingslanding.pks.lab.winterfell.live/artifactory
concourse_uri: https://ci.lab.winterfell.live
artifactory-build-name: todos-webflux
artifactory-repo: libs-release-local
code-repo-uri: git@github.com:doddatpivotal/todos-webflux.git
code-repo-branch: master
code-repo-group-id: io.todos
code-repo-artifact-id: todos-webflux
code-repo-owner: doddatpivotal
code-repo-repository: todos-webflux
code-repo-access-token: SuperSecretAccessToken
version-repo-uri: git@github.com:doddatpivotal/todos-webflux.git
version-repo-branch: version
code-repo-private-key: |
  -----BEGIN OPENSSH PRIVATE KEY----- fake
  fake TEST-KEY-CONTENTS fake
  -----END OPENSSH PRIVATE KEY-----
cf-test-api: https://api.run.pcfone.io
cf-test-username: username
cf-test-password: SuperSecretPassword
cf-test-org: pivot-dpfeffer
cf-test-space: test
cf-test-route: dodd-todos-test.apps.pcfone.io
cf-prod-api: https://api.run.pcfone.io
cf-prod-username: username
cf-prod-password: SuperSecretPassword
cf-prod-org: pivot-dpfeffer
cf-prod-space: production
cf-prod-route: dodd-todos-prod.apps.pcfone.io
```
### CI/CD

- Test unit test task

```bash
fly -t lab execute -c ci/tasks/unit-test.yml -i code-repo=. -i ci-scripts=.
```

- Test build and publish task

```bash
mkdir -p target/version
echo "1.0.0-rc.1" > target/version/version

export REPO_WITH_BINARIES_FOR_UPLOAD=$ENTER_YOUR_URI
export M2_SETTINGS_REPO_USERNAME=$ENTER_YOUR_USERNAME
export M2_SETTINGS_REPO_PASSWORD=$ENTER_YOUR_PASSWORD

REPO_WITH_BINARIES_FOR_UPLOAD=$REPO_WITH_BINARIES_FOR_UPLOAD \
    M2_SETTINGS_REPO_USERNAME=$M2_SETTINGS_REPO_USERNAME \
    M2_SETTINGS_REPO_PASSWORD=$M2_SETTINGS_REPO_PASSWORD \
    fly -t lab execute -c ci/tasks/build-and-publish.yml -i code-repo=. \
        -i ci-scripts=. \
        -i version=target/version \
         --include-ignored
```

- Deploy pipeline

```bash
fly -t lab login -k

fly -t lab set-pipeline  -p todos-webflux \
    --config ci/pipeline.yml \
    --load-vars-from ci/.secrets.yml \
    --non-interactive

fly -t lab unpause-pipeline -p todos-webflux
```
 
## Considerations when designing a pipeline

There are many considerations to take into account.  Only some of them have been answered here.

- How to manage versions
  - Separate repo, or the same.  May want to use seperate if driving change to trigger a bunch of microservices at the same time
  - When to update and when to bump
- Where to put release artifacts
  - S3, github, artifactory, nowhere
- How to integrate with cf
  - Cf resource or Cf-cli-resource or other
  - Write your own scripts
- How far into continuous delivery will you go?
  - How many cf spaces?
- How much do you want to incorporate testing
  - Smoke tests in the pipeline
  - Cloud-pipeline techniques alike rollback testing and api compatibility testing
- Would you like to use a full featured CD tool like Spinnaker for the complexities
- Store scripts in code repo or not
- Select the right resources
- Select the right base-image to run your tasks
- Matching release versions to maven versions: SNAPSHOT or not to SNAPSHOT
- Pipelines for libraries (where deployment ends with release to artifactory)
- Guarding against inclusion of SNAPSHOT dependencies
- Integration with secure code tools like Fortify
- Integration with code quality tools like SonarQube
- Integration with change management systems like ServiceNow
- Naming conventions
- What can run in parallel, and what should be serial



