package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec
import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI
import tech.thatgravyboat.skyblockapi.api.datetime.SkyBlockSeason
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils

data class SeasonCondition(private val seasons: MutableSet<SkyBlockSeason>) : Condition {

    override val id: String = "season"
    override fun test(): Boolean = DateTimeAPI.season in this.seasons

    companion object {

        val CODEC: MapCodec<SeasonCondition> = RecordCodecBuilder.mapCodec { it.group(
            CodecUtils.set(EnumCodec.of(SkyBlockSeason::class.java)).fieldOf("seasons").forGetter(SeasonCondition::seasons)
        ).apply(it, ::SeasonCondition) }
    }
}