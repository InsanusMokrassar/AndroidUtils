package com.github.insanusmokrassar.androidutils.front.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent

fun Context.createAndStartActivity(activityClass: Class<out Activity>, flags: Int = 0) {
    val intent = Intent(this, activityClass)
    intent.flags = flags
    startActivity(intent)
}
