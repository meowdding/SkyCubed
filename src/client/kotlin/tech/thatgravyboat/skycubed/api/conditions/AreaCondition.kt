package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skycubed.utils.Codecs.asMapCodec

data class AreaCondition(private val area: SkyBlockArea) : Condition {
    override val id: String = "area"
    override fun test(): Boolean = LocationAPI.area == area

    companion object {
        val CODEC: MapCodec<AreaCondition> = Codec.STRING
            .xmap(::SkyBlockArea, SkyBlockArea::name)
            .xmap(::AreaCondition, AreaCondition::area)
            .asMapCodec("area")
    }
}
