package com.github.insanusmokrassar.SimpleAndroidViewsManagers.EditText.string

import android.widget.EditText
import com.github.insanusmokrassar.SimpleAndroidViewsManagers.EditText.EditTextManager

open class RegexStringEditTextManager(
        view: EditText,
        validationRegex: Regex = Regex("^.*$"),
        textTransformationSet: (String, EditTextManager<String>) -> String = { it, _ -> it },
        textTransformationGet: (String, EditTextManager<String>) -> String = { it, _ -> it },
        onTextChangedValidation: Boolean = true
) : StringEditTextManager(
        view,
        {
            s, _ ->
            validationRegex.matches(s)
        },
        textTransformationSet,
        textTransformationGet,
        onTextChangedValidation
)
