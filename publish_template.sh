#!/bin/bash
#################################################
#
# IT IS JUST TEMPLATE
# IF YOU WANT TO USE THIS PUBLISH FILE - REPLASE
# BINTRAY_USER AND BINTRAY_KEY IN NEW publish.sh
# FILE

# ALL ARGUMENTS WILL DETECTING AS NAMES OF MODULES
for MODULE_NAME in $@ ; do
    echo Publish $MODULE_NAME module
    ./gradlew ${MODULE_NAME}:clean ${MODULE_NAME}:build ${MODULE_NAME}:bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
done
