# RecyclerViewItemsLeft

This module was created as part of AndroidUtils support library which contain only one class to work with notifying about left items before the end of list in `RecyclerView`.

Just call `RecyclerView#subscribeItemsLeft` with `(callback: (Int) -> Unit, leftItems: Int)` or `(callback: (Int) -> Unit, filter: (Int) -> Boolean)` for filter left itens and be notified when you need it.
