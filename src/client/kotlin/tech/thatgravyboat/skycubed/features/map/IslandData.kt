package tech.thatgravyboat.skycubed.features.map

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.EnumCodec
import tech.thatgravyboat.skycubed.api.conditions.LocationCondition
import tech.thatgravyboat.skycubed.api.conditions.LocationConditions
import tech.thatgravyboat.skycubed.features.map.pois.Poi
import tech.thatgravyboat.skycubed.features.map.texture.MapImage

data class IslandData(
    val island: SkyBlockIsland,
    val default: MapImage,
    val images: List<Pair<LocationCondition, MapImage>>,
    val topX: Int,
    val topY: Int,
    val bottomX: Int,
    val bottomY: Int,
    val offsetX: Int,
    val offsetY: Int,
    val playerOffsetX: Int,
    val playerOffsetY: Int,
    val pois: List<Poi>
) {

    private val cache: Cache<BlockPos, MapImage> = CacheBuilder.newBuilder()
        .maximumSize(50)
        .build()

    val width = bottomX - topX
    val height = bottomY - topY

    init {
        pois.forEach {
            it.position.add(offsetX, offsetY)
        }
    }

    fun getDefaultTexture(): MapImage = default

    fun getTexture(): MapImage {
        if (island != LocationAPI.island) return default
        val pos = McPlayer.self?.blockPosition() ?: return default
        return cache.get(pos) { images.find { it.first(pos) }?.second ?: default }
    }

    companion object {

        private val IMAGES_CODEC: Codec<Pair<LocationCondition, MapImage>> = RecordCodecBuilder.create { it.group(
            LocationConditions.CODEC.fieldOf("conditions").forGetter(Pair<LocationCondition, MapImage>::first),
            MapImage.CODEC.fieldOf("image").forGetter(Pair<LocationCondition, MapImage>::second)
        ).apply(it, ::Pair) }

        val CODEC: Codec<IslandData> = RecordCodecBuilder.create { it.group(
            EnumCodec.of(SkyBlockIsland.entries.toTypedArray()).fieldOf("island").forGetter(IslandData::island),
            MapImage.CODEC.fieldOf("default").forGetter(IslandData::getDefaultTexture),
            IMAGES_CODEC.listOf().optionalFieldOf("images", listOf()).forGetter(IslandData::images),
            Codec.INT.fieldOf("topX").forGetter(IslandData::topX),
            Codec.INT.fieldOf("topY").forGetter(IslandData::topY),
            Codec.INT.fieldOf("bottomX").forGetter(IslandData::bottomX),
            Codec.INT.fieldOf("bottomY").forGetter(IslandData::bottomY),
            Codec.INT.optionalFieldOf("offsetX", 0).forGetter(IslandData::offsetX),
            Codec.INT.optionalFieldOf("offsetY", 0).forGetter(IslandData::offsetY),
            Codec.INT.optionalFieldOf("playerOffsetX", 0).forGetter(IslandData::playerOffsetX),
            Codec.INT.optionalFieldOf("playerOffsetY", 0).forGetter(IslandData::playerOffsetY),
            Poi.CODEC.listOf().optionalFieldOf("pois", listOf()).forGetter(IslandData::pois)
        ).apply(it, ::IslandData) }
    }
}