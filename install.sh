#!/usr/bin/env bash

function assert_success() {
    "${@}"
    local status=${?}
    if [ ${status} -ne 0 ]; then
        echo "### Error ${status} at: ${BASH_LINENO[*]} ###"
        exit ${status}
    fi
}

assert_success ./gradlew clean cleanBuildCache CommonAndroidUtils:install RecyclerViewAdapter:install RecyclerViewItemsLeft:install SimpleAndroidViewsManagers:install
assert_success ./gradlew clean cleanBuildCache SimpleAndroidORM:install SimpleAndroidRequests:install SimpleAndroidAppsHelper:install
