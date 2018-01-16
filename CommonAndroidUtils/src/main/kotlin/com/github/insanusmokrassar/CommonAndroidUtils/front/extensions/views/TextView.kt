package com.github.insanusmokrassar.CommonAndroidUtils.front.extensions.views

import android.graphics.Color
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView

fun TextView.setLinkText(
        text: String,
        onClickAction: (View) -> Unit = {},
        underline: Boolean = true,
        color: Int? = Color.BLUE
) {
    this.text = if (underline) {
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, text.length, 0)
        content
    } else {
        text
    }
    color ?. let {
        this.setTextColor(color)
    }
    this.setOnClickListener(onClickAction)
}
