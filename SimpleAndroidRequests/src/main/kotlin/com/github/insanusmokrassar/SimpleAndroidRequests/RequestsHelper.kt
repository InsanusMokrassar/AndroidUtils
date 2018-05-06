package com.github.insanusmokrassar.SimpleAndroidRequests

import android.app.Application
import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.insanusmokrassar.IObjectK.extensions.asMap
import com.github.insanusmokrassar.IObjectK.interfaces.IObject
import com.github.insanusmokrassar.IObjectK.realisations.SimpleIObject
import com.github.insanusmokrassar.IObjectKRealisations.toStringMap
import com.github.insanusmokrassar.IObjectK.extensions.iterator
import com.github.insanusmokrassar.IObjectK.interfaces.IInputObject
import java.util.concurrent.ConcurrentLinkedQueue

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
    var defaultRetryPolicyCreator: ((Request<*>) -> RetryPolicy) = { DefaultRetryPolicy() }
        set(value) {
            Log.d(this::class.java.simpleName, "Default retry policy was changed for: $this; value: $value")
            field = value
        }

    private val executeQueue = Volley.newRequestQueue(c)
    private val requestsQueue = ConcurrentLinkedQueue<Request<*>>()
    private val blockRequestsQueue = ConcurrentLinkedQueue<Request<*>>()

    private var currentRequest: Request<*>? = null
        @Synchronized
        set(value) {
            val oldValue = field
            field = value
            value ?.let {
                executeQueue.add(it)
            } ?:let {
                oldValue ?.let { blockRequestsQueue.remove(it) }
                triggerExecuteRequest()
            }
        }

    init {
        executeQueue.addRequestFinishedListener<Any> {
            Log.i("Requests listener", "Request $it is finished")
            currentRequest = null
        }
        executeQueue.start()
    }


    @Synchronized
    fun execute(
            url: String,
            method: Int,
            successResponse: (String) -> Unit,
            errorListener: Response.ErrorListener,
            paramsBuilder: () -> IInputObject<String, Any> = { SimpleIObject() },
            priority: Request.Priority = Request.Priority.NORMAL,
            retryPolicy: RetryPolicy? = null,
            block: Boolean = false
    ) {
        Log.i("Requests bus", "Try to add request for: $url")
        val request = SimpleRequest(
                url,
                method,
                successResponse,
                errorListener,
                paramsBuilder,
                priority
        ).also {
            it.retryPolicy = retryPolicy ?: defaultRetryPolicyCreator(it)
        }
        execute(request, block)
    }

    @Synchronized
    fun <T> execute(
            request: Request<T>,
            block: Boolean = false
    ) {
        try {
            request.sequence = executeQueue.sequenceNumber
            if (block) {
                blockRequestsQueue.offer(request)
                Log.d(RequestsHelper::class.java.simpleName, "Blocked queue: $blockRequestsQueue")
            } else {
                requestsQueue.offer(request)
                Log.d(RequestsHelper::class.java.simpleName, "Async queue: $requestsQueue")
            }
            triggerExecuteRequest()
        } catch (e: NullPointerException) {
            Log.e("Requests bus", "RequestsQueue is not ready", e)
        }
    }

    private fun triggerExecuteRequest() {
        currentRequest ?:let {
            while (blockRequestsQueue.isEmpty() && requestsQueue.isNotEmpty()) {
                executeQueue.add(requestsQueue.poll())
            }
            if (blockRequestsQueue.isNotEmpty()) {
                currentRequest = blockRequestsQueue.peek()
            }
        }
    }
    
    fun stop() {
        requestsQueue.clear()
        executeQueue.cancelAll { true }
    }
}

open class SimpleRequest(
        url: String,
        method: Int,
        successResponse: (String) -> Unit,
        errorListener: Response.ErrorListener,
        private val paramsBuilder: () -> IInputObject<String, Any> = { SimpleIObject() },
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
    private val realParams: IInputObject<String, Any>
        get() {
            val params = SimpleIObject(paramsBuilder().asMap())
            super.getParams() ?.let {
                params.putAll(it)
            }
            return params
        }

    private val adaptedParams: IObject<Any>
        get() {
            return if (method == Request.Method.POST) {
                val result = SimpleIObject()
                realParams.iterator().forEach {
                    val value = it.second
                    when (value) {
                        is Iterable<*> -> value.forEachIndexed {
                            i, current ->
                            result["${it.first}[$i]"]=current!!
                        }
                        else -> result[it.first] = it.second
                    }
                }
                result
            } else {
                SimpleIObject(realParams.asMap())
            }
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
        return adaptedParams.toStringMap().toMutableMap()
    }

    override fun getPriority(): Priority = priority
}

fun constructServerRequestUrl(
        c: Context,
        requestUrlTemplateResId: Int,
        requestUrlTemplatePostfixResId: Int,
        vararg params: String
): String {
    return constructServerRequestUrl(
        c.getString(requestUrlTemplateResId),
        c.getString(requestUrlTemplatePostfixResId),
        *params
    )
}

fun constructServerRequestUrl(
        requestUrlTemplate: String,
        requestUrlTemplatePostfix: String,
        vararg params: String
): String {
    return String.format(
            requestUrlTemplate,
            String.format(
                    requestUrlTemplatePostfix,
                    *params
            )
    )
}
