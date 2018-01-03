package com.github.insanusmokrassar.CommonAndroidUtils.common.extensions

import com.github.insanusmokrassar.IObjectKRealisations.doUsingDefaultGSON
import kotlin.reflect.KClass

fun <T: Any> String.toObject(clazz: KClass<T>): T {
    return doUsingDefaultGSON {
        it.fromJson(this, clazz.java)
    }
}
