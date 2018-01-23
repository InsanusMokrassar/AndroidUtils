package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.EditTextManager

import android.widget.EditText
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

open class EditTextManager<T>(
        private val view: EditText,
        private val validChecker: (T, EditTextManager<T>) -> Boolean = {
            _, _ ->
            true
        },
        private val textTransformationSet: (T, EditTextManager<T>) -> String,
        private val textTransformationGet: (String, EditTextManager<T>) -> T
) {
    var text: T?
        get() = textTransformationGet(
                view.text.toString(),
                this
        )
        set(value) {
            value ?.let {
                launch (UI) {
                    val text = textTransformationSet(it, this@EditTextManager)
                    view.text.apply {
                        clear()
                        insert(0, text)
                    }
                }
            } ?:let {
                launch (UI) {
                    view.text.clear()
                }
            }
        }

    val isCorrect: Boolean
        get() = text ?.let {
            validChecker(it, this)
        } ?: false
    val correctOrNull: T?
        get() = if (isCorrect) {
            text
        } else {
            null
        }
    var error: String?
        get() = view.error.toString()
        set(value) {
            launch (UI) {
                view.error = value
            }
        }
}

