package com.github.insanusmokrassar.SimpleAndroidRequests

import android.app.Application
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.insanusmokrassar.IObjectK.extensions.asMap
import com.github.insanusmokrassar.IObjectK.interfaces.IObject
import com.github.insanusmokrassar.IObjectK.realisations.SimpleIObject
import com.github.insanusmokrassar.IObjectKRealisations.toStringMap
import com.github.insanusmokrassar.CommonAndroidUtils.common.extensions.TAG

private val cache = HashMap<String, RequestsHelper>()

fun Application.getRequestsHelper(): RequestsHelper {
    synchronized(cache, {
        return cache[this.packageName] ?: {
            val current = RequestsHelper(this)
            cache[this.packageName] = current
            current
        }()
    })
}

class RequestsHelper internal constructor (c: Context) {
    private val executeQueue = Volley.newRequestQueue(c)
    private var readyToSend = true
    private val requestsQueue = ArrayList<Request<String>>()

    private val sync = Object()
    private val syncQueuesThread = Thread({
        synchronized(sync, {
            try {
                while (true) {
                    while (requestsQueue.isEmpty() || !readyToSend) {
                        sync.wait()
                    }
                    executeQueue.add(requestsQueue.removeAt(0))
                    readyToSend = false
                }
            } catch (e: Exception) {
                Log.w(TAG(), "REQUESTS SYNCHRONISATION WAS STOPPED", e)
            }
        })
    })

    init {
        executeQueue.addRequestFinishedListener<Any> {
            Log.i("Requests listener", "Request $it is finished")
            synchronized(sync, {
                readyToSend = true
                sync.notify()
            })
        }
        executeQueue.start()
        syncQueuesThread.start()
    }


    fun execute(
            url: String,
            method: Int,
            successResponse: (String) -> Unit,
            errorListener: Response.ErrorListener,
            paramsBuilder: () -> IObject<Any> = { SimpleIObject() },
            priority: Request.Priority = Request.Priority.NORMAL
    ) {
        try {
            Log.i("Requests bus", "Try to add request for: $url")
            synchronized(sync, {
                val request = SimpleRequest(
                        url,
                        method,
                        successResponse,
                        errorListener,
                        paramsBuilder,
                        priority
                )
                request.sequence = executeQueue.sequenceNumber
                requestsQueue.add(request)
                requestsQueue.sortWith(
                        Comparator { first, second -> first.compareTo(second) }
                )
                sync.notify()
            })
        } catch (e: NullPointerException) {
            Log.e("Requests bus", "RequestsQueue is not ready", e)
        }
    }

    fun stop() {
        syncQueuesThread.interrupt()
        requestsQueue.clear()
        executeQueue.cancelAll { true }
    }
}

class SimpleRequest(
        url: String,
        method: Int,
        successResponse: (String) -> Unit,
        errorListener: Response.ErrorListener,
        private val paramsBuilder: () -> IObject<Any> = { SimpleIObject() },
        private val priority: Request.Priority = Request.Priority.NORMAL
): StringRequest(
        method,
        url,
        Response.Listener {
            Log.i(SimpleRequest::class.java.simpleName, "Response:\n$it")
            successResponse(it)
        },
        errorListener
) {

    private val realParams: IObject<Any>
        get() {
            val params = paramsBuilder()
            super.getParams()?.let {
                params.putAll(it)
            }
            return params
        }

    override fun getUrl(): String {
        val currentUrl = super.getUrl()
        return if (method == Request.Method.GET) {
            val argsStringBuilder = StringBuilder()
            val params = realParams
            params.asMap().forEach {
                current ->
                val value = current.value
                when(value) {
                    is Iterable<*> -> value.forEach {
                        argsStringBuilder.append("&${current.key}=$it")
                    }
                    else -> argsStringBuilder.append("&${current.key}=$value")
                }
            }
            if (argsStringBuilder.isNotEmpty()) {
                "$currentUrl?${argsStringBuilder.removeRange(0, 1)}"
            } else {
                currentUrl
            }
        } else {
            currentUrl
        }

    }

    override fun getParams(): MutableMap<String, String> {
        return realParams.toStringMap().toMutableMap()
    }

    override fun getPriority(): Priority = priority
}

fun constructServerRequestUrl(
        c: Context,
        requestUrlTemplateResId: Int,
        requestUrlTemplatePostfixResId: Int,
        vararg params: String
): String {
    return String.format(
            c.getString(requestUrlTemplateResId),
            String.format(
                    c.getString(requestUrlTemplatePostfixResId),
                    *params
            )
    )
}

