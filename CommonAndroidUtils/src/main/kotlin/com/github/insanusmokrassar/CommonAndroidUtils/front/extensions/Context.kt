package com.github.insanusmokrassar.CommonAndroidUtils.front.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

fun Context.createAndStartActivity(activityClass: Class<out Activity>, flags: Int = 0) {
    val intent = Intent(this, activityClass)
    intent.flags = flags
    startActivity(intent)
}

fun Context.defaultShortToast(
        text: String
) {
    launch (UI) {
        Toast.makeText(
                this@defaultShortToast,
                text,
                Toast.LENGTH_SHORT
        ).show()
    }
}

fun Context.defaultShortToast(
        textRes: Int
) {
    defaultShortToast(getString(textRes))
}

fun Context.defaultLongToast(
        text: String
) {
    launch (UI) {
        Toast.makeText(
                this@defaultLongToast,
                text,
                Toast.LENGTH_LONG
        ).show()
    }
}

fun Context.defaultLongToast(
        textRes: Int
) {
    defaultLongToast(getString(textRes))
}
