package com.github.insanusmokrassar.SimpleAndroidViewsManagers.EditText

import android.widget.EditText
import com.github.insanusmokrassar.SimpleAndroidViewsManagers.EditText.EditTextManager

open class StringEditTextManager(
        view: EditText,
        validChecker: (String, EditTextManager<String>) -> Boolean = { _, _ -> true },
        textTransformationSet: (String, EditTextManager<String>) -> String = { it, _ -> it },
        textTransformationGet: (String, EditTextManager<String>) -> String = { it, _ -> it },
        editTextValidator: (String) -> Boolean = { true }
) : EditTextManager<String>(
        view, validChecker, textTransformationSet, textTransformationGet, editTextValidator
)
