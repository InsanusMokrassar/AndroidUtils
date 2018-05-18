package com.github.insanusmokrassar.RecyclerViewItemsLeft

import android.util.Log
import kotlinx.coroutines.experimental.launch

typealias LeftItemsCallback = (Int) -> Unit
typealias LeftItemsFilter = (Int) -> Boolean

internal class LeftItemsSubscription : (Int) -> Unit {

    private val filters = HashMap<LeftItemsFilter, MutableSet<LeftItemsCallback>>()

    fun addFilter(filter: LeftItemsFilter, callback: LeftItemsCallback) {
        (filters[filter] ?: HashSet<LeftItemsCallback>().also {
            filters[filter] = it
        }).add(callback)
    }

    override fun invoke(next: Int) {
        filters.keys.forEach {
            checker ->
            launch {
                try {
                    if (checker(next)) {
                        filters[checker] ?.forEach {
                            launch {
                                try {
                                    it(next)
                                } catch (e: Exception) {
                                    Log.d(
                                        this@LeftItemsSubscription::class.java.simpleName,
                                        "Can't handle \"left\" event by $it",
                                        e
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d(
                        this@LeftItemsSubscription::class.java.simpleName,
                        "Can't check event update need by $checker",
                        e
                    )
                }
            }
        }
    }
}