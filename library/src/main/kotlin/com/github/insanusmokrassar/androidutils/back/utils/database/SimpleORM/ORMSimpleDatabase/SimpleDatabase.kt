package com.github.insanusmokrassar.androidutils.back.utils.database.SimpleORM.ORMSimpleDatabase

import android.content.Context
import android.util.Log
import com.github.insanusmokrassar.androidutils.R
import com.github.insanusmokrassar.androidutils.back.utils.database.SimpleORM.ContentProvider.CommonSQLiteContentObserver
import com.github.insanusmokrassar.androidutils.back.utils.database.SimpleORM.ContentProvider.providerUri
import com.github.insanusmokrassar.androidutils.back.utils.database.SimpleORM.extractAll
import com.github.insanusmokrassar.androidutils.back.utils.database.SimpleORM.getPrimaryFields
import com.github.insanusmokrassar.androidutils.back.utils.database.SimpleORM.getPrimaryFieldsSearchQuery
import com.github.insanusmokrassar.androidutils.back.utils.database.SimpleORM.toContentValues
import com.github.insanusmokrassar.androidutils.common.extensions.TAG
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

open class SimpleDatabase<M: Any> (
        private val modelClass: KClass<M>,
        context: Context,
        databaseName: String,
        version: Int,
        private val defaultOrderBy: String? = null
) {
    private val contextReference = WeakReference(context)
    private val providerUri =
            providerUri(
                    context.getString(R.string.contentProviderAuthority),
                    databaseName,
                    modelClass,
                    version
            )

    //Temporary without observables
//    private val subject = PublishSubject.create<SimpleDatabase<M>>()
//
//    val observable: Observable<SimpleDatabase<M>> = subject.debounce(
//            200L,
//            TimeUnit.MILLISECONDS
//    )
//
//    init {
//        observable.subscribe {
//            Log.i(TAG(), "Changed: $it")
//        }
//    }
//
//    private val providerObserver = CommonSQLiteContentObserver {
//        if (providerUri == it) {
//            subject.onNext(this)
//        }
//    }
//
//    init {
//        context.contentResolver.registerContentObserver(
//                providerUri,
//                false,
//                providerObserver
//        )
//    }

    fun insert(value: M): Boolean {
        return contextReference.get() ?.let {
            it.contentResolver.insert(
                    providerUri,
                    value.toContentValues()
            ) != null
        } ?: throw IllegalArgumentException("Context was destroyed")
    }

    fun find(
            where: String? = null,
            orderBy: String? = defaultOrderBy,
            offset: Int? = null,
            size: Int = 20
    ): List<M> {
        return contextReference.get() ?.let {
            val list = it.contentResolver.query(
                    providerUri,
                    null,
                    where,
                    null,
                    orderBy
            ).extractAll(modelClass, true)
            offset ?.let {
                list.subList(it, it + size)
            } ?: list
        } ?: throw IllegalArgumentException("Context was destroyed")
    }

    fun find(value: M): M? =
            find(value.getPrimaryFieldsSearchQuery()).firstOrNull()

    fun findPage(page: Int, size: Int, orderBy: String? = defaultOrderBy): List<M> =
            find(page * size,size, orderBy)

    fun find(page: Int, size: Int, orderBy: String? = defaultOrderBy): List<M> =
            find(null, orderBy, page * size, size)

    fun update(
            value: M,
            where: String? = value.getPrimaryFieldsSearchQuery()
    ): Boolean {
        return contextReference.get() ?.let {
            it.contentResolver.update(
                    providerUri,
                    value.toContentValues(),
                    where,
                    null
            ) > 0
        } ?: throw IllegalArgumentException("Context was destroyed")
    }
    fun remove(where: String? = null): Boolean = remove(find(where))

    fun remove(vararg elements: M): Boolean = remove(listOf(*elements))

    fun remove(elements: Iterable<M>): Boolean {
        return contextReference.get() ?.let {
            it.contentResolver.delete(
                    providerUri,
                    elements.getPrimaryFieldsSearchQuery(),
                    null
            ) > 0
        } ?: throw IllegalArgumentException("Context was destroyed")
    }

    fun size(where: String? = null): Long {
        return contextReference.get() ?.let {
            it.contentResolver.query(
                    providerUri,
                    modelClass.getPrimaryFields().map { it.name }.toTypedArray(),
                    where,
                    null,
                    null
            ).count.toLong()
        } ?: throw IllegalArgumentException("Context was destroyed")
    }
}
