#!/bin/bash
set -e
source ${BASH_SOURCE%/*}/common.sh
cd cumulocity-sdk
../mvnw clean install -s $MVN_SETTINGS -U
cd -