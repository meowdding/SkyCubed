package tech.thatgravyboat.skycubed.utils

import org.lwjgl.glfw.GLFW

data class KeyboardKeys(
    val symbols: Set<String>,
    val keys: Set<Int>
) {

    fun isKey(key: Int, scanCode: Int): Boolean {
        return key in keys || GLFW.glfwGetKeyName(key, scanCode) in symbols
    }
}

fun keysOf(vararg keys: Int) = KeyboardKeys(
    keys = keys.toSet(),
    symbols = emptySet()
)

fun keysOf(keys: Set<Int>, symbols: Set<String>): KeyboardKeys = KeyboardKeys(
    keys = keys,
    symbols = symbols
)
