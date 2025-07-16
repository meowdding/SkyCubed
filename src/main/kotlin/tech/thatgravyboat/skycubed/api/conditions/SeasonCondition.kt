package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec
import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI
import tech.thatgravyboat.skyblockapi.api.datetime.SkyBlockSeason

data class SeasonCondition(private val season: SkyBlockSeason) : Condition {

    override val id: String = "season"
    override fun test(): Boolean = DateTimeAPI.season == this.season

    companion object {

        val CODEC: MapCodec<SeasonCondition> = RecordCodecBuilder.mapCodec { it.group(
            EnumCodec.of(SkyBlockSeason::class.java).fieldOf("season").forGetter(SeasonCondition::season)
        ).apply(it, ::SeasonCondition) }
    }
}