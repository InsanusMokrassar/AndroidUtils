package com.github.insanusmokrassar.androidutils.back.extensions

import android.content.Context
import android.os.Build
import java.util.*

fun Context.getCacheDirectoryPath(): String {
    return externalCacheDir?.let {
        return it.absolutePath
    } ?: cacheDir.absolutePath
}

fun Context.locale(): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        resources.configuration.locale
    }
}
