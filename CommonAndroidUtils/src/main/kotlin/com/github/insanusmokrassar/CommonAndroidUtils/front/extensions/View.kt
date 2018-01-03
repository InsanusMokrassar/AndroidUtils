package com.github.insanusmokrassar.CommonAndroidUtils.front.extensions

import android.util.Log
import android.view.View
import com.github.insanusmokrassar.CommonAndroidUtils.R
import com.github.insanusmokrassar.CommonAndroidUtils.common.extensions.TAG
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


fun <R : View> View.findNestedViewById(vararg viewsIds : Int): R? {
    var current: View = this
    try {
        viewsIds.forEach {
            if (viewsIds.last() == it) {
                return current.findViewById(it)
            } else {
                current = current.findViewById<View>(it)
            }
        }
    } catch (e : Exception) {
        Log.e(TAG(), "Can not resolve nested view: $viewsIds; in $this")
    }
    return null
}

fun View.showProgressBar() {
    launch (UI) {
        findViewById<View>(R.id.progressBar) ?.let {
            it.visibility = View.VISIBLE
        }
    }
}

fun View.hideProgressBar() {
    launch (UI) {
        findViewById<View>(R.id.progressBar) ?.let {
            it.visibility = View.GONE
        }
    }
}
