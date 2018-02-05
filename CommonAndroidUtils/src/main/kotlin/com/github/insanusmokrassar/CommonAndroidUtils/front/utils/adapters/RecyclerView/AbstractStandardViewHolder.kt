package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.adapters.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

@Deprecated("Class was replaced into other module", ReplaceWith("Dependency 'com.github.insanusmokrassar:RecyclerViewAdaper'"))
abstract class AbstractStandardViewHolder<T>(
        inflater: LayoutInflater,
        container: ViewGroup?,
        viewId: Int,
        onViewInflated: (View) -> Unit = { }
) : AbstractViewHolder<T>({
    val view = inflater.inflate(viewId, container, false)
    onViewInflated(view)
    view
})
