package com.github.insanusmokrassar.CommonAndroidUtils.front.extensions.views

import android.graphics.Color
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView

fun TextView.setLinkText(
        text: String,
        onClickAction: (View) -> Unit = {},
        color: Int = Color.BLUE
) {
    val content = SpannableString(text)
    content.setSpan(UnderlineSpan(), 0, text.length, 0)
    this.text = content
    this.setTextColor(color)
    this.setOnClickListener(onClickAction)
}
