package com.github.insanusmokrassar.RecyclerViewItemsLeft

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import java.util.*

private val weakLeftItemsMap = WeakHashMap<RecyclerView, LeftItemsSubscription>()

fun RecyclerView.subscribeItemsLeft(
        callback: LeftItemsCallback,
        leftPagesCount: Int
) {
    if (leftPagesCount < 0) {
        throw IllegalArgumentException("leftPagesCount must be positive")
    }
    subscribeItemsLeft(
            callback,
            {
                it <= leftPagesCount
            }
    )
}

fun RecyclerView.subscribeItemsLeft(
        callback: LeftItemsCallback,
        filter: LeftItemsFilter
) {
    (weakLeftItemsMap[this] ?:let {
        val layoutManager = layoutManager
        when (layoutManager) {
            is LinearLayoutManager -> LeftItemsSubscription().also {
                subscription ->
                weakLeftItemsMap[this] = subscription
                addOnScrollListener(
                    LinearLayoutManagerLeftItemsListener(
                        layoutManager,
                        {
                            subscription(it)
                        }
                    )
                )
            }
            else -> throw IllegalStateException("RecyclerView must use ${LinearLayoutManager::class.java.canonicalName} or it")
        }
    }).addFilter(filter, callback)
}

private class LinearLayoutManagerLeftItemsListener(
        private val layoutManager: LinearLayoutManager,
        private val callback: LeftItemsCallback
) : RecyclerView.OnScrollListener() {
    private var lastVisible: Int = -1
        set(value) {
            if (field != value) {
                field = value
                val maxPosition = layoutManager.itemCount - 1
                callback(maxPosition - lastVisible)
            }
        }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        lastVisible = layoutManager.findLastVisibleItemPosition()
    }
}
