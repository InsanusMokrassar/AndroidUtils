package com.github.insanusmokrassar.androidutils.front.helpers

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

fun Context.createSimpleTextDialog(
        titleRes: Int,
        textRes: Int,
        positiveButtonTextRes: Int = 0,
        negativeButtonRes: Int = 0,
        positiveCallback: (DialogInterface) -> Unit = { it.dismiss() },
        negativeCallback: (DialogInterface) -> Unit = { it.cancel() },
        show: Boolean = true
): AlertDialog {
    val builder = AlertDialog.Builder(this)

    builder.setTitle(titleRes)
            .setMessage(textRes)
    if (positiveButtonTextRes != 0) {
        builder.setPositiveButton(positiveButtonTextRes, { di, _ -> positiveCallback(di) })
    }
    if (negativeButtonRes != 0) {
        builder.setNegativeButton(negativeButtonRes, { di, _ -> negativeCallback(di) })
    }

    val dialog = builder.create()

    if (show) {
        dialog.show()
    }
    return dialog
}
