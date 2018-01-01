package com.github.insanusmokrassar.androidutils.front.utils.adapters.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup

abstract class AbstractStandardViewHolder<T>(
        inflater: LayoutInflater,
        container: ViewGroup?,
        viewId: Int
) : AbstractViewHolder<T>({
    inflater.inflate(viewId, container, false)
})
