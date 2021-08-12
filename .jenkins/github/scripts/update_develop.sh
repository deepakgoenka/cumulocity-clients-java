#!/usr/bin/env bash

hotfix_version=$1
development_version=$2

git checkout githubdev
cd cumulocity-sdk
git checkout githubdev
cd -

find . -name 'pom.xml' | xargs sed -i "s/<version>${hotfix_version}<\\/version>/<version>${development_version}<\\/version>/g"
.jenkins/common/scripts/update_dependencies.sh ${development_version}

git commit -am 'Update dependencies to next SNAPSHOT version' --allow-empty
git push --follow-tags https://${GITHUB_USER}:${GITHUB_PASSWORD}@github.com/SoftwareAG/cumulocity-clients-java githubdev

cd cumulocity-sdk

git commit -am 'Update to dependencies next SNAPSHOT version' --allow-empty
git push --follow-tags https://${GHES_USER}:${GHES_PASSWORD}@github.softwareag.com/IOTA/cumulocity-sdk githubdev

cd -

.jenkins/common/scripts/deploy.sh
