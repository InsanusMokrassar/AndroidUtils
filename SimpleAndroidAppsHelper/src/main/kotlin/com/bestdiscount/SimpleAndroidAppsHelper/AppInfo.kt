package com.bestdiscount.SimpleAndroidAppsHelper

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppInfo(
        private val packageInfo: PackageInfo,
        private val packagesManager: PackageManager
) {

    private var cachedIcon: Drawable? = null
    private var cachedName: String? = null

    val packageName: String = packageInfo.packageName
    val version: String? = packageInfo.versionName

    val name: String
        get() = cachedName ?: packageInfo.applicationInfo.loadLabel(packagesManager).toString().also {
            cachedName = it
        }

    val icon: Drawable
        get() = cachedIcon ?: packageInfo.applicationInfo.loadIcon(packagesManager).also {
            cachedIcon = it
        }
}
