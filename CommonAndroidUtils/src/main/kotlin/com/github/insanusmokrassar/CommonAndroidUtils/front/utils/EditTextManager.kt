package com.github.insanusmokrassar.CommonAndroidUtils.front.utils

import android.widget.EditText
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class EditTextManager(
        private val view: EditText,
        private val validChecker: (String) -> Boolean = { true },
        private val textTransformationSet: (String) -> String = { it },
        private val textTransformationGet: (String) -> String = { it }
) {
    var text: String?
        get() = textTransformationGet(
                view.text.toString()
        )
        set(value) {
            value ?.let {
                launch (UI) {
                    val text = textTransformationSet(it)
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
            validChecker(it)
        } ?: false
    val correctOrNull: String?
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
