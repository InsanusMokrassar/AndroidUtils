package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.adapters.RecyclerView

import android.support.v7.widget.RecyclerView
import android.view.View

@Deprecated("Class was replaced into other module", ReplaceWith("Dependency 'com.github.insanusmokrassar:RecyclerViewAdaper'"))
abstract class AbstractViewHolder<in T>(
        viewCreator: () -> View
) : RecyclerView.ViewHolder(viewCreator()) {
    abstract fun refreshItem(item: T)
}
