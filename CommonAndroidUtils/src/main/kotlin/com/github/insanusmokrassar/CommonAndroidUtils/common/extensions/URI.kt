package com.github.insanusmokrassar.CommonAndroidUtils.common.extensions

import android.net.Uri
import java.net.URI

fun URI.toAndroidUri(): Uri {
    return Uri.Builder().scheme(scheme)
            .encodedAuthority(rawAuthority)
            .encodedPath(rawPath)
            .query(rawQuery)
            .fragment(rawFragment)
            .build()
}
