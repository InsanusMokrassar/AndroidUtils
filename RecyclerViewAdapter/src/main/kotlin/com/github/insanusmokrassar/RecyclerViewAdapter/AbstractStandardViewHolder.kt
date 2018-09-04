package com.github.insanusmokrassar.RecyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class AbstractStandardViewHolder<T>(
        inflater: LayoutInflater,
        container: ViewGroup?,
        viewId: Int,
        onViewInflated: ((View) -> Unit)? = null
) : AbstractViewHolder<T>(
    inflater.inflate(viewId, container, false).also {
        onViewInflated ?.invoke(it)
    }
)
