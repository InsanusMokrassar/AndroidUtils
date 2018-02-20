package com.github.insanusmokrassar.SimpleAndroidViewsManagers.EditText.string

import android.widget.EditText
import com.github.insanusmokrassar.SimpleAndroidViewsManagers.EditText.EditTextManager

open class RegexStringEditTextManager(
        view: EditText,
        validationRegex: Regex = Regex("^.*$"),
        textTransformationSet: (String, EditTextManager<String>) -> String = { it, _ -> it },
        textTransformationGet: (String, EditTextManager<String>) -> String = { it, _ -> it },
        onTextInvalidCallback: (String, EditTextManager<String>) -> Unit = { _, _ -> },
        onTextChangedValidationRegex: Regex? = null
) : StringEditTextManager(
        view,
        {
            s, _ ->
            validationRegex.matches(s)
        },
        textTransformationSet,
        textTransformationGet,
        onTextInvalidCallback,
        onTextChangedValidationRegex ?.let {
            {
                s: String ->
                it.matches(s)
            }
        }
)
