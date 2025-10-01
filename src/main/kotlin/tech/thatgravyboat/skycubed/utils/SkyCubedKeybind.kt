package tech.thatgravyboat.skycubed.utils

import me.owdding.lib.utils.MeowddingKeybind
import tech.thatgravyboat.skycubed.SkyCubed

class SkyCubedKeybind(
    translationKey: String,
    keyCode: Int,
    allowMultipleExecutions: Boolean = false,
    runnable: (() -> Unit)? = null,
) : MeowddingKeybind(SkyCubed.id("map"), translationKey, keyCode, allowMultipleExecutions, runnable)
