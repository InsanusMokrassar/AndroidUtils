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
        private val onTextInvalidCallback: (String, EditTextManager<T>) -> Unit,
        textChangedValidator: ((String) -> Boolean)? = null
) {
    var text: String
        get() = view.text.toString()
        set(value) {
            view.text.clear()
            view.text.insert(0, value)
        }

    var data: T?
        get() = textTransformationGet(
                text,
                this
        )
        set(value) {
            value ?.let {
                launch (UI) {
                    text = textTransformationSet(it, this@EditTextManager)
                }
            } ?:let {
                launch (UI) {
                    text = ""
                }
            }
        }

    private val isCorrect: Boolean
        get() = data ?.let {
            validChecker(it, this).apply {
                if (!this) {
                    onTextInvalidCallback(text, this@EditTextManager)
                }
            }
        } ?: false

    val correctOrNull: T?
        get() = if (isCorrect) {
            data
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

