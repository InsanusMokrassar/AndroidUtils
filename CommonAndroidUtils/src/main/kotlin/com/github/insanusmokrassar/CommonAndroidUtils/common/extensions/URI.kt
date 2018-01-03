package com.github.insanusmokrassar.CommonAndroidUtils.common.extensions

import android.net.Uri
import java.net.URI

fun URI.toAndroidUri(): Uri {
    return Uri.Builder().scheme(getScheme())
            .encodedAuthority(getRawAuthority())
            .encodedPath(getRawPath())
            .query(getRawQuery())
            .fragment(getRawFragment())
            .build()
}
