package com.github.insanusmokrassar.RecyclerViewAdapter

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

abstract class RecyclerViewAdapter<T>(
        val data: List<T>
): RecyclerView.Adapter<AbstractViewHolder<T>>() {
    var emptyView: View? = null
        set(value) {
            field = value
            checkEmpty()
        }

    init {
        registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        super.onItemRangeChanged(positionStart, itemCount)
                        checkEmpty()
                    }

                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                        super.onItemRangeChanged(positionStart, itemCount, payload)
                        checkEmpty()
                    }

                    override fun onChanged() {
                        super.onChanged()
                        checkEmpty()
                    }

                    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                        super.onItemRangeRemoved(positionStart, itemCount)
                        checkEmpty()
                    }

                    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                        checkEmpty()
                    }

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        checkEmpty()
                    }
                }
        )
        checkEmpty()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: AbstractViewHolder<T>, position: Int) {
        holder.onBind(data[position])
    }

    private fun checkEmpty() {
        emptyView ?. let {
            if (data.isEmpty()) {
                launch(UI) {
                    it.visibility = View.VISIBLE
                }
            } else {
                launch(UI) {
                    it.visibility = View.GONE
                }
            }
        }
    }
}
