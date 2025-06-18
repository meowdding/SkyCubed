package tech.thatgravyboat.skycubed.utils

import com.teamresourceful.resourcefulconfigkt.api.Entry
import com.teamresourceful.resourcefulconfigkt.api.builders.EntriesBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.TypeBuilder

fun <T, B : TypeBuilder> EntriesBuilder.observable2(
    entry: Entry<T, B>,
    onChange: () -> Unit
) = this.observable(entry) { _, _ -> onChange() }
