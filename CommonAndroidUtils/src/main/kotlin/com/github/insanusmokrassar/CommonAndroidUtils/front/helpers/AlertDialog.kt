package com.github.insanusmokrassar.CommonAndroidUtils.front.helpers

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.github.insanusmokrassar.CommonAndroidUtils.R
import com.github.insanusmokrassar.CommonAndroidUtils.front.utils.adapters.RecyclerView.AbstractViewHolder
import com.github.insanusmokrassar.CommonAndroidUtils.front.utils.adapters.RecyclerView.RecyclerViewAdapter

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

fun <T> Context.createRecyclerViewDialog(
        data: List<T>,
        titleRes: Int,
        viewHolderFactory: (
                parent: ViewGroup,
                viewType: Int,
                adapter: RecyclerViewAdapter<T>
        ) -> AbstractViewHolder<T>,
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
    val adapter = RecyclerViewAdapter(viewHolderFactory, data)
    recyclerView.adapter = adapter

    val builder = AlertDialog.Builder(this)

    builder.setView(recyclerView)
            .setTitle(titleRes)

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

    val editText = EditText(this)
    val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    )
    editText.layoutParams = lp
    editText.inputType = inputType

    editTextHintRes ?. let {
        editText.setHint(it)
    }

    val builder = AlertDialog.Builder(this)

    builder.setView(editText)
            .setPositiveButton(
                    this.getString(android.R.string.ok),
                    {
                        dialog, _ ->
                        if (callback(editText.text.toString())) {
                            dialog.dismiss()
                        }
                    }
            )
    titleRes ?.let {
        builder.setTitle(it)
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
        viewCreator: (Context) -> T,
        positivePair: Pair<Int, (DialogInterface) -> Unit>? = null,
        negativePair: Pair<Int, (DialogInterface) -> Unit>? = null,
        show: Boolean = true
): AlertDialog {
    val builder = AlertDialog.Builder(this)

    builder.setView(viewCreator(this))

    positivePair ?. let {
        builder.setPositiveButton(getString(it.first), { di, _ -> it.second(di) })
    }
    negativePair ?. let {
        builder.setNegativeButton(getString(it.first), { di, _ -> it.second(di) })
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        setOnDismissListener {
            if (!checker()) {
                show()
            }
        }
    }
    return this
}
