package com.github.insanusmokrassar.CommonAndroidUtils.back.utils

import android.content.Context
import android.content.SharedPreferences
import com.github.insanusmokrassar.IObjectK.interfaces.IObject
import com.github.insanusmokrassar.IObjectK.realisations.ConcurrentSimpleCommonIObject
import com.github.insanusmokrassar.IObjectK.realisations.SimpleIObject
import java.io.Serializable

private val cache = HashMap<String, MutableMap<String, KeyValueStore>>()

fun Context.keyValueStore(
        name: String = "default"
): IObject<Any> {
    val className = this::class.java.simpleName
    return cache[className] ?. get(name) ?.let {
        it
    } ?: {
        cache.put(
                className,
                mutableMapOf(
                        Pair(
                                name,
                                KeyValueStore(this, name)
                        )
                )
        )
        keyValueStore(name)
    }()
}

class KeyValueStore internal constructor (
        c: Context,
        preferencesName: String
) : IObject<Any>, SharedPreferences.OnSharedPreferenceChangeListener {
    private val sharedPreferences = c.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    private val cachedData = SimpleIObject()

    init {
        sharedPreferences.all.forEach {
            if (it.value != null) {
                cachedData.put(it.key, it.value as Any)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
        val value = sp.all[key]
        if (value != null) {
            cachedData.put(key, value)
        } else {
            cachedData.remove(key)
        }
    }

    @Synchronized
    override fun put(key: String, value: Any) {
        sharedPreferences.edit()
                .put(key, value)
                .apply()
    }

    @Synchronized
    override fun <T: Any> get(key: String): T {
        val value = cachedData.get<Any>(key)
        return when(value) {
            !is String -> value
            else -> {
                if (canBeSerializable(value)) {
                    try {
                        deserialize<Serializable>(value)
                    } catch (e: ClassCastException) {
                        value
                    }
                } else {
                    value
                }
            }
        } as T
    }

    @Synchronized
    override fun keys(): Set<String> {
        return cachedData.keys().toSet()
    }

    @Synchronized
    override fun putAll(toPutMap: Map<String, Any>) {
        val editor = sharedPreferences.edit()
        toPutMap.forEach {
            editor.put(it.key, it.value)
        }
        editor.apply()
    }

    @Synchronized
    override fun remove(key: String) {
        sharedPreferences.edit()
                .remove(key)
                .apply()
    }

    @Synchronized
    private fun SharedPreferences.Editor.put(key: String, value: Any): SharedPreferences.Editor {
        when(value) {
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Set<*> -> try {
                putStringSet(key, value as Set<String>)
            } catch (e: ClassCastException) {
                putStringSet(key, LinkedHashSet(value.map { it.toString() }))
            }
            is Serializable -> putString(key, serialize(value))
            else -> throw IllegalArgumentException(
                    "You can put into SPIObject only: Int, Long, Float, String, Boolean or some set"
            )
        }
        return this
    }
}

