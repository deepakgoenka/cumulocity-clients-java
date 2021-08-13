#!/usr/bin/env bash

hotfix_version=$1
development_version=$2

git checkout develop
cd cumulocity-sdk
git checkout develop
cd -

find . -name 'pom.xml' | xargs sed -i "s/<version>${hotfix_version}<\\/version>/<version>${development_version}<\\/version>/g"
.jenkins/scripts/update_dependencies.sh ${development_version}

git commit -am 'Update dependencies to next SNAPSHOT version' --allow-empty
git push --follow-tags https://${BITBUCKET_USER}:${BITBUCKET_PASSWORD}@bitbucket.org/m2m/cumulocity-clients-java develop

cd cumulocity-sdk

git commit -am 'Update to dependencies next SNAPSHOT version' --allow-empty
git push --follow-tags https://${BITBUCKET_USER}:${BITBUCKET_PASSWORD}@bitbucket.org/m2m/cumulocity-sdk develop

cd -

.jenkins/scripts/deploy.sh
