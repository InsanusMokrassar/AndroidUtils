#!/usr/bin/env bash

./gradlew clean cleanBuildCache CommonAndroidUtils:install RecyclerViewAdapter:install RecyclerViewItemsLeft:install SimpleAndroidViewsManagers:install
./gradlew clean cleanBuildCache SimpleAndroidORM:install SimpleAndroidRequests:install SimpleAndroidAppsHelper:install
