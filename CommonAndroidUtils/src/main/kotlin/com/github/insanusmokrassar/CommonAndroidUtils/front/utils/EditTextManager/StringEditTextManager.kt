package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.EditTextManager

import android.widget.EditText

open class StringEditTextManager(
        view: EditText,
        validChecker: (String, EditTextManager<String>) -> Boolean = { _, _ -> true },
        textTransformationSet: (String, EditTextManager<String>) -> String = { it, _ -> it },
        textTransformationGet: (String, EditTextManager<String>) -> String = { it, _ -> it },
        editTextValidator: (String) -> Boolean = { true }
) : EditTextManager<String>(
        view, validChecker, textTransformationSet, textTransformationGet, editTextValidator
)
