package com.bestdiscount.SimpleAndroidAppsHelper

import android.content.Context
import java.lang.ref.WeakReference
import java.util.*

private val helpers = WeakHashMap<Context, AppsHelper>()

fun Context.appsHelper(): AppsHelper {
    synchronized(helpers, {
        helpers[this]?.let { return it }
        helpers[this] = AppsHelper(this)
        return helpers[this]!!
    })
}

class AppsHelper internal constructor(context: Context) {
    private val contextWR = WeakReference(context)

    val installed: List<AppInfo>?
        get() = contextWR.get() ?. packageManager ?.run {
            getInstalledPackages(0).map { AppInfo(it, this) }
        }

    operator fun contains(packageName: String): Boolean {
        return installed ?. firstOrNull { it.packageName == packageName } != null
    }
}
