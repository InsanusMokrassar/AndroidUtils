package com.github.insanusmokrassar.androidutils.front.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.github.insanusmokrassar.androidutils.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


fun Activity.checkPermissions() {
    val info = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
    val notGrantedPermissions = info.requestedPermissions.filter {
        ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }
    if (notGrantedPermissions.isEmpty()) {
        return
    }
    ActivityCompat.requestPermissions(
            this,
            notGrantedPermissions.toTypedArray(),
            0
    )
}

fun Activity.getRootView(): View = findViewById(android.R.id.content)

fun Activity.showProgressBar() {
    launch (UI) {
        findViewById<View>(R.id.progressBar) ?.let {
            it.visibility = View.VISIBLE
        }
    }
}

fun Activity.hideProgressBar() {
    launch (UI) {
        findViewById<View>(R.id.progressBar) ?.let {
            it.visibility = View.GONE
        }
    }
}
