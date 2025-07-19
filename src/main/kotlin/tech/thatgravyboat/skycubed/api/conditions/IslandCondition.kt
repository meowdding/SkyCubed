package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

data class IslandCondition(private val island: SkyBlockIsland) : Condition {

    override val id: String = "island"
    override fun test(): Boolean = this.island.inIsland()

    companion object {

        val CODEC: MapCodec<IslandCondition> = RecordCodecBuilder.mapCodec { it.group(
            EnumCodec.of(SkyBlockIsland::class.java).fieldOf("island").forGetter(IslandCondition::island)
        ).apply(it, ::IslandCondition) }
    }
}