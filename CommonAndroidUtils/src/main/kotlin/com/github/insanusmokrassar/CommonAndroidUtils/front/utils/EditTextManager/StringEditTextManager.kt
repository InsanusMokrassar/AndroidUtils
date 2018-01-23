package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.EditTextManager

import android.widget.EditText

class StringEditTextManager(
        view: EditText,
        validChecker: (String) -> Boolean = { true },
        textTransformationSet: (String) -> String = { it },
        textTransformationGet: (String) -> String = { it }
) : EditTextManager<String>(
        view, validChecker, textTransformationSet, textTransformationGet
)
