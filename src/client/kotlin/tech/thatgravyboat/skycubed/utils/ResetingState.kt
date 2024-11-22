package tech.thatgravyboat.skycubed.utils

import earth.terrarium.olympus.client.utils.State

class ResetingState<T>(private val initializer: () -> T) : State<T> {

    private var value: T = initializer()

    override fun get(): T = value
    override fun set(p0: T) {
        value = p0
    }

    fun reset() {
        value = initializer()
    }

    companion object {
        fun <T> of(initializer: () -> T): ResetingState<T> = ResetingState(initializer)
    }
}