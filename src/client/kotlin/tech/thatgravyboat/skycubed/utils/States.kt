package tech.thatgravyboat.skycubed.utils

import earth.terrarium.olympus.client.utils.State

class ResettingState<T>(private val initializer: () -> T) : State<T> {

    private var value: T = initializer()

    override fun get(): T = value
    override fun set(p0: T) {
        value = p0
    }

    fun reset() {
        value = initializer()
    }

    companion object {
        fun <T> of(initializer: () -> T): ResettingState<T> = ResettingState(initializer)
    }
}

class GettingState<T>(private val getter: () -> T) : State<T> {
    override fun get(): T = getter()
    override fun set(p0: T) {}

    companion object {
        fun <T> of(getter: () -> T): GettingState<T> = GettingState(getter)
    }
}
