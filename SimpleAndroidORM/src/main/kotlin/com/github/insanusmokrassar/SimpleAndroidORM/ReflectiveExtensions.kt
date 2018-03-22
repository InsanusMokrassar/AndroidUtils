package com.github.insanusmokrassar.SimpleAndroidORM

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class PrimaryKey

@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class Autoincrement

const val descOrder = "DESC"
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class OrderBy(val order: String = "ASC")

/**
 * List of classes which can be primitive
 */
val nativeTypes = listOf(
        Int::class,
        Long::class,
        Float::class,
        Double::class,
        String::class,
        Boolean::class
)

/**
 * @return Экземпляр KClass, содержащий данный KCallable объект.
 */
fun <T> KCallable<T>.intsanceKClass() : KClass<*> =
        this.instanceParameter?.type?.classifier as KClass<*>

/**
 * @return true если значение параметра может быть null.
 */
fun KCallable<*>.isNullable() : Boolean =
        this.returnType.isMarkedNullable

/**
 * @return Экземпляр KClass, возвращаемый KCallable.
 */
fun KCallable<*>.returnClass() : KClass<*> =
        this.returnType.classifier as KClass<*>

/**
 * @return true, если возвращает некоторый примитив.
 */
fun KCallable<*>.isReturnNative() : Boolean =
        nativeTypes.contains(this.returnClass())

/**
 * @return true если объект помечен аннотацией
 */
fun KProperty<*>.isAnnotated(annotationClass: KClass<*>) : Boolean {
    this.annotations.forEach {
        if (it.annotationClass == annotationClass) {
            return true
        }
    }
    return false
}

/**
 * @return true если объект помечен аннотацией [PrimaryKey].
 */
fun KProperty<*>.isPrimaryField() : Boolean = isAnnotated(PrimaryKey::class)

/**
 * @return true если объект помеченc аннотацией [Autoincrement].
 */
fun KProperty<*>.isAutoincrement() : Boolean = isAnnotated(Autoincrement::class)

/**
 * @return Список полей класса.
 */
fun KClass<*>.getVariables() : List<KProperty<*>> =
        this.memberProperties.toList()

/**
 * @return Список полей класса, участвующих в построении порядка вывода данных
 */
fun KClass<*>.getOrdersBy(): List<KProperty<*>> {
    return getVariables().filter {
        it.isAnnotated(OrderBy::class)
    }
}
