package com.github.insanusmokrassar.CommonAndroidUtils.front.helpers

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import com.github.insanusmokrassar.CommonAndroidUtils.R

fun Context.createSimpleTextDialog(
        title: String,
        text: String,
        positiveButtonTextRes: Int = 0,
        negativeButtonRes: Int = 0,
        positiveCallback: (DialogInterface) -> Unit = { it.dismiss() },
        negativeCallback: (DialogInterface) -> Unit = { it.cancel() },
        show: Boolean = true
): AlertDialog {
    val builder = AlertDialog.Builder(this)

    builder.setTitle(title)
            .setMessage(text)
    if (positiveButtonTextRes != 0) {
        builder.setPositiveButton(positiveButtonTextRes, { di, _ -> positiveCallback(di) })
    }
    if (negativeButtonRes != 0) {
        builder.setNegativeButton(negativeButtonRes, { di, _ -> negativeCallback(di) })
    }

    return if (show) {
        val dialog = builder.create()
        dialog.show()
        dialog
    } else {
        builder.create()
    }
}

fun Context.createRecyclerViewDialog(
        titleRes: Int,
        adapterFactory: () -> RecyclerView.Adapter<*>,
        layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this),
        show: Boolean = true
): AlertDialog {
    return createRecyclerViewDialog(
        getString(titleRes),
        adapterFactory,
        layoutManager,
        show
    )
}

fun Context.createRecyclerViewDialog(
    title: String,
    adapterFactory: () -> RecyclerView.Adapter<*>,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this),
    show: Boolean = true
): AlertDialog {
    val recyclerView = RecyclerView(this)
    val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    )
    val marginOfRecyclerView = resources.getDimension(R.dimen.viewDefaultMediumMargin).toInt()
    lp.setMargins(
            marginOfRecyclerView,
            marginOfRecyclerView,
            marginOfRecyclerView,
            marginOfRecyclerView
    )
    recyclerView.layoutManager = layoutManager
    recyclerView.layoutParams = lp
    recyclerView.adapter = adapterFactory()

    val builder = AlertDialog.Builder(this)

    builder.setTitle(title)
            .setView(recyclerView)

    return if (show) {
        val dialog = builder.create()
        dialog.show()
        dialog
    } else {
        builder.create()
    }
}

fun Context.createEditTextDialog(
        callback: (String) -> Boolean,
        titleRes: Int? = null,
        editTextHintRes: Int? = null,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        show: Boolean = true
): AlertDialog {
    return createEditTextDialog(
            callback,
            titleRes ?.let { getString(it) },
            editTextHintRes ?.let { getString(it) },
            inputType,
            show
    )
}

fun Context.createEditTextDialog(
        callback: (String) -> Boolean,
        title: String? = null,
        editTextHint: String? = null,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        show: Boolean = true
): AlertDialog {

    val editText = AppCompatEditText(this)
    val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    )
    editText.layoutParams = lp
    editText.inputType = inputType

    editTextHint ?. let {
        editText.hint = it
    }

    val builder = AlertDialog.Builder(this)
    var canBeClosed = true
    builder.setView(editText)
            .setPositiveButton(
                    this.getString(android.R.string.ok),
                    {
                        _, _ ->
                        canBeClosed = callback(editText.text.toString())
                    }
            )
    title ?.let {
        builder.setTitle(it)
    }
    val dialog = builder.create()

    dialog.setDismissChecker {
        val toReturn = canBeClosed
        canBeClosed = true
        toReturn
    }

    if (show) {
        dialog.show()
    }
    return dialog
}

fun Context.createSimpleCheckBoxesDialog(
        title: String? = null,
        variants: List<String>,
        checked: MutableList<String> = mutableListOf(),
        positivePair: Pair<Int, ((DialogInterface) -> Unit)?>? = null,
        negativePair: Pair<Int, ((DialogInterface) -> Unit)?>? = null,
        show: Boolean = true
): AlertDialog {
    val builder = AlertDialog.Builder(this)

    title ?.let {
        builder.setTitle(title)
    }

    builder.setMultiChoiceItems(
            variants.toTypedArray(),
            variants.map { checked.contains(it) }.toBooleanArray(),
            {
                dialog, i, isChecked ->
                if (isChecked) {
                    checked.add(variants[i])
                } else {
                    checked.remove(variants[i])
                }
            }
    )

    positivePair ?. let {
        builder.setPositiveButton(getString(it.first), { di, _ -> it.second ?. invoke(di) })
    }
    negativePair ?. let {
        builder.setNegativeButton(getString(it.first), { di, _ -> it.second ?. invoke(di) })
    }

    return if (show) {
        val dialog = builder.create()
        dialog.show()
        dialog
    } else {
        builder.create()
    }
}

fun <T: View> Context.createCustomViewDialog(
        title: String? = null,
        viewCreator: (Context) -> T,
        positivePair: Pair<Int, ((DialogInterface) -> Unit)?>? = null,
        negativePair: Pair<Int, ((DialogInterface) -> Unit)?>? = null,
        show: Boolean = true
): AlertDialog {
    val builder = AlertDialog.Builder(this)

    title ?.let {
        builder.setTitle(title)
    }

    builder.setView(viewCreator(this))

    positivePair ?. let {
        builder.setPositiveButton(getString(it.first), { di, _ -> it.second ?. invoke(di) })
    }
    negativePair ?. let {
        builder.setNegativeButton(getString(it.first), { di, _ -> it.second ?. invoke(di) })
    }

    return if (show) {
        val dialog = builder.create()
        dialog.show()
        dialog
    } else {
        builder.create()
    }
}

fun AlertDialog.setDismissChecker(checker: () -> Boolean) : AlertDialog {
    setOnDismissListener {
        if (!checker()) {
            show()
        }
    }
    return this
}
