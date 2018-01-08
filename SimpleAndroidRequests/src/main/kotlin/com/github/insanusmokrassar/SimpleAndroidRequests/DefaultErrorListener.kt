package com.github.insanusmokrassar.SimpleAndroidRequests

import com.android.volley.Response
import com.android.volley.VolleyError
import java.nio.charset.Charset

class DefaultErrorListener<T> (
        val defaultHandler: (T) -> Unit = { },
        val errorConverter: (String) -> List<T> = { emptyList() },
        val handlers: Map<T, (T) -> Unit> = emptyMap()
): Response.ErrorListener, (String) -> Unit {
    override fun invoke(errorString: String) {
        errorConverter(
                errorString
        ).forEach {
            error ->
            handlers[error] ?.let {
                it(error)
            } ?: defaultHandler(error)
        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        error ?.let {
            invoke(
                    it
                            .networkResponse
                            .data
                            .toString(Charset.defaultCharset())
            )
        }
    }
}
