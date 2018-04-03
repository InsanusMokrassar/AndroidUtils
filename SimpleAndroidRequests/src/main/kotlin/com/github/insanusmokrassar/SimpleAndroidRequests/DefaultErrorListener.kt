package com.github.insanusmokrassar.SimpleAndroidRequests

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import java.nio.charset.Charset

fun tryToExtractServerErrorBody(
        error: VolleyError,
        charset: Charset = Charset.defaultCharset()
): String? {
    return try {
        error
                .networkResponse
                .data
                .toString(
                        charset
                )
    } catch (e: Exception) {
        null
    }
}

class DefaultErrorListener<T> (
        private val defaultHandler: (T) -> Unit = { },
        private val errorConverter: (VolleyError?) -> List<T> = { emptyList() },
        private val handlers: Map<T, (T) -> Unit> = emptyMap()
): Response.ErrorListener {
    override fun onErrorResponse(error: VolleyError?) {
        errorConverter(
                error
        ).forEach {
            error ->
            try {
                handlers[error]?.let {
                    it(error)
                }
            } catch (e: Exception) {
                Log.d(this::class.java.simpleName, "Can't handle error by handler: $error")
            } ?:let {
                try {
                    defaultHandler(error)
                } catch (e: Exception) {
                    Log.d(this::class.java.simpleName, "Can't handle error by default handler: $error")
                }
            }
        }
    }
}
