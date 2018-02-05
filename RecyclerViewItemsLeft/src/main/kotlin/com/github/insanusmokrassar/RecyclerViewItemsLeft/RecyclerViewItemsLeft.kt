package com.github.insanusmokrassar.RecyclerViewItemsLeft

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

typealias LeftItemsCallback = (Int) -> Unit
typealias LeftItemsSubscription = Observable<Int>

private val weakLeftItemsMap = WeakHashMap<RecyclerView, LeftItemsSubscription>()

fun RecyclerView.subscribeItemsLeft(
        callback: LeftItemsCallback,
        leftPagesCount: Int
) {
    if (leftPagesCount < 0) {
        throw IllegalArgumentException("leftPagesCount must be positive")
    }
    weakLeftItemsMap[this] ?.let {
        it.filter {
            it <= leftPagesCount
        }.subscribe(callback)
    } ?:let {
        weakLeftItemsMap[this] = PublishSubject.create<Int>().also {
            subject ->
            val layoutManager = layoutManager
            when (layoutManager) {
                is LinearLayoutManager -> addOnScrollListener(
                        LinearLayoutManagerLeftItemsListener(
                                layoutManager,
                                {
                                    subject.onNext(it)
                                }
                        )
                )
            }
        }
        subscribeItemsLeft(callback, leftPagesCount)
    }
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
