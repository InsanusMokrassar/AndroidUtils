package com.github.insanusmokrassar.SimpleAndroidRequests

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*

private val managers = HashMap<String, CacheManager>()

private fun Context.getCacheDirectoryPath(): String {
    return (externalCacheDir ?: cacheDir).absolutePath
}

fun Context.imagesManager(): CacheManager {
    val path = getCacheDirectoryPath()
    synchronized(managers, {
        managers[path]?.let { return it }
        managers[path] = CacheManager(this)
        return managers[path]!!
    })
}

fun Context.closeCacheManager() {
    val path = getCacheDirectoryPath()
    synchronized(managers, {
        managers[path]?.let {
            it.close()
            managers.remove(path)
        }
    })
}

fun Application.closeAllCacheManagers() {
    managers.forEach {
        it.value.close()
        managers.remove(it.key)
    }
}

private fun URL.toImageCacheFilePath(absolutePath: String): String {
    return String.format(
            "%s/%s",
            absolutePath,
            this.path
    )
}


@Throws(IOException::class)
private fun createFile(absolutePath: String): File {
    var result = File(absolutePath)
    val fileName = result.name
    result = File(absolutePath.replace(fileName, ""))
    result.mkdirs()
    result = File(absolutePath)
    result.createNewFile()

    return result
}

class CacheManager internal constructor(
        context: Context,
        absolutePath: String = context.getCacheDirectoryPath()
) {
    private val requestsQueue: RequestQueue = Volley.newRequestQueue(context)
    private val cache = ImageCache(absolutePath)
    private val loader = ImageLoader(requestsQueue, cache)

    var defaultImageRes: Int = R.drawable.ic_default_image
    var brokenImageRes: Int = R.drawable.ic_broken_image

    fun loadImage(requestUrl: String,
                  targetView: ImageView,
                  defaultResource: Int = defaultImageRes,
                  errorResource: Int = brokenImageRes,
                  maxWidth: Int? = null,
                  maxHeight: Int? = null,
                  scaleType: ImageView.ScaleType? = ImageView.ScaleType.CENTER_INSIDE
    ) {
        val imageListener = ImageLoader.getImageListener(
                targetView,
                defaultResource,
                errorResource
        )
        if (maxWidth != null && maxHeight != null) {
            loader.get(
                    requestUrl, imageListener, maxWidth, maxHeight, scaleType
            )
        } else {
            loader.get(
                    requestUrl,
                    imageListener
            )
        }
    }

    fun invalidateUrl(requestUrl: String) {
        cache.forceList.add(requestUrl)
    }

    internal fun close() {
        requestsQueue.cancelAll { true }
        requestsQueue.stop()
    }
}

private val cachePrefixRegex = Regex("^(#W\\d*)?(#H\\d*)?(#S\\d*)?")

private class ImageCache(private val absolutePath: String): ImageLoader.ImageCache {
    var forceList: MutableList<String> = ArrayList()
    private val inMemoryCache = WeakHashMap<String, Bitmap>()

    override fun getBitmap(url: String): Bitmap? {
        val filePath = url.toAbsolutePath()
        return if (File(filePath).exists() &&
                !forceList.contains(cachePrefixRegex.replaceFirst(url, ""))
        ) {
            inMemoryCache[filePath] ?: BitmapFactory.decodeFile(filePath).apply {
                inMemoryCache[filePath] = this
            }
        } else {
            null
        }
    }

    override fun putBitmap(url: String, bitmap: Bitmap) {
        createFile(url.toAbsolutePath()).outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun String.toAbsolutePath(): String {
        val prefix = cachePrefixRegex.find(this) ?. groupValues ?. firstOrNull() ?: ""
        val filePath = URL(
                this.replaceFirst(cachePrefixRegex, "")
        ).toImageCacheFilePath(
                absolutePath
        )
        val file = File(filePath)
        return file.parent.plus("/").plus("$prefix/${file.name}")
    }
}

