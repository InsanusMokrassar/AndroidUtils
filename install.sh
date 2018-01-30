#!/usr/bin/env bash

./gradlew clean cleanBuildCache CommonAndroidUtils:install
./gradlew clean cleanBuildCache SimpleAndroidORM:install SimpleAndroidRequests:install SimpleAndroidAppsHelper:install
