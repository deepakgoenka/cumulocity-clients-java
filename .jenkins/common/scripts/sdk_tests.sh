##!/bin/bash

DEPLOYMENT_ENVIRONMENT = $1

source /etc/profile.d/java.sh
cd java-client && mvn clean install -Pintegration -Dcumulocity.host=http://${DEPLOYMENT_ENVIRONMENT}




    