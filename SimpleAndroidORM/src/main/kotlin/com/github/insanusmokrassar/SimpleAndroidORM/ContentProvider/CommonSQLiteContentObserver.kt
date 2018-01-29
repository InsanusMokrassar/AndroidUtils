package com.github.insanusmokrassar.SimpleAndroidORM.ContentProvider

import android.database.ContentObserver
import android.net.Uri

class CommonSQLiteContentObserver(
        private val callback: (Uri?) -> Unit
) : ContentObserver(null) {
    override fun deliverSelfNotifications(): Boolean = true

    override fun onChange(selfChange: Boolean) {
        callback(null)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        callback(uri)
    }
}
