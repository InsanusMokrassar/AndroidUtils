package com.github.insanusmokrassar.SimpleAndroidViewsManagers.EditText

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

open class EditTextManager<T>(
        val view: EditText,
        private val validChecker: (T, EditTextManager<T>) -> Boolean = {
            _, _ ->
            true
        },
        private val textTransformationSet: (T, EditTextManager<T>) -> String,
        private val textTransformationGet: (String, EditTextManager<T>) -> T,
        textChangedValidator: ((String) -> Boolean)? = null
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

    init {
        textChangedValidator ?. let {
            view.addTextChangedListener(
                StandardEditTextManagerWatcher(
                        it
                )
            )
        }
    }
}

private class StandardEditTextManagerWatcher(
        private val validator: (String) -> Boolean
): TextWatcher {
    private var oldText: String? = null

    override fun afterTextChanged(editable: Editable) {
        if (!validator(editable.toString())) {
            editable.replace(
                    0,
                    editable.length,
                    oldText
            )
        }
    }

    override fun beforeTextChanged(oldText: CharSequence?, p1: Int, p2: Int, p3: Int) {
        this.oldText = oldText ?. toString()
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}

