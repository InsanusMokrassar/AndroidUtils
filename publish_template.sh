#!/bin/bash

# Here first argument will be detected as name of module
PROJECT_NAME=$1
./gradlew ${PROJECT_NAME}:clean ${PROJECT_NAME}:build ${PROJECT_NAME}:bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
