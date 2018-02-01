package com.bestdiscount.SimpleAndroidAppsHelper

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppInfo(
        val packageInfo: PackageInfo,
        val name: String,
        val packageName: String = packageInfo.packageName,
        val version: String? = packageInfo.versionName,
        val icon: Drawable
) {
    constructor(
            packageInfo: PackageInfo,
            packagesManager: PackageManager
    ) : this(
            packageInfo,
            packageInfo.applicationInfo.loadLabel(packagesManager).toString(),
            icon = packageInfo.applicationInfo.loadIcon(packagesManager)
    )
}
