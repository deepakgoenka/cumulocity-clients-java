#!/usr/bin/env bash


function update-status {
    commitId=${MERCURIAL_REVISION}
    status=${1}
    name=${2}
    key=$( echo $3 | rev | cut -c -40 | rev )
    echo "Sending build status update for  commitId: ${commitId}, status: ${status}, name: ${name} , key: ${key}"
    echo $(curl -s --user ${BITBUCKET_USER}:${BITBUCKET_PASSWORD} -H "Content-Type: application/json" -X POST https://api.bitbucket.org/2.0/repositories/m2m/cumulocity-clients-java/commit/${commitId}/statuses/build --data "{\"state\":\"${status}\", \"name\": \"${name}\", \"key\": \"${key}\", \"url\": \"http://localhost:8081/job/Cumulocity-JavaSDK/job/${BRANCH_NAME}/${BUILD_ID}/\", \"description\": \"\" }")
}

update-status $1 $2 $3 
