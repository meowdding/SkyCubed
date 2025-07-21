package tech.thatgravyboat.skycubed.utils

import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible
import kotlin.time.Duration

class CachedValue<T>(private val cacheTime: Duration, private val supplier: () -> T) {

    private var value: T? = null
    private var lastUpdate: Long = 0

    operator fun getValue(thisRef: Any?, property: Any?): T = get()

    fun get(): T {
        if (value == null || System.currentTimeMillis() - lastUpdate > cacheTime.inWholeMilliseconds) {
            value = supplier()
            lastUpdate = System.currentTimeMillis()
        }
        return value!!
    }

    fun invalidate() {
        value = null
    }

    operator fun invoke(): T = get()
}

fun KProperty0<*>.invalidateCache() {
    this.isAccessible = true
    (this.getDelegate() as? CachedValue<*>)?.invalidate()
}
