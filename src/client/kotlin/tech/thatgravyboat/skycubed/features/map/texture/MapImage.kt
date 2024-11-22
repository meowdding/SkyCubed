package tech.thatgravyboat.skycubed.features.map.texture

import com.mojang.serialization.Codec
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.renderer.texture.SimpleTexture
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.helpers.McClient

class MapImage(private val url: String) {

    private val id: String = DownloadedAsset.getUrlHash(url)
    private val location: ResourceLocation = ResourceLocation.fromNamespaceAndPath("skycubed_map", this.id)
    private var texture: SimpleTexture? = null

    fun getId(): ResourceLocation {
        if (this.texture == null) {
            this.texture = DownloadedTexture(
                FabricLoader.getInstance().configDir.resolve("skycubed").resolve("map").resolve(this.id).toFile(),
                url,
                this.location
            )
            McClient.self.textureManager.register(this.location, this.texture!!)
        }
        return this.location
    }

    companion object {

        val CODEC: Codec<MapImage> = Codec.STRING.xmap(::MapImage, MapImage::url)
    }
}