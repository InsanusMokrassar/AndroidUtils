#!/usr/bin/env bash

./gradlew clean cleanBuildCache CommonAndroidUtils:install RecyclerViewAdapter:install RecyclerViewItemsLeft:install
./gradlew clean cleanBuildCache SimpleAndroidORM:install SimpleAndroidRequests:install SimpleAndroidAppsHelper:install
