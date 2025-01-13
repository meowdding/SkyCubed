package tech.thatgravyboat.skycubed.features.map.texture

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.serialization.Codec
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.features.map.texture.DownloadedAsset.runDownload
import java.io.File
import java.io.FileInputStream

class MapImage(private val url: String) {

    private val id: String = DownloadedAsset.getUrlHash(url)
    private val location: ResourceLocation = ResourceLocation.fromNamespaceAndPath("skycubed_map", this.id)
    private val file: File = FabricLoader.getInstance().configDir.resolve("skycubed").resolve("map").resolve(this.id).toFile()

    private var uploaded: Boolean = false
    private var image: NativeImage? = null

    private fun loadFromFile() {
        this.image = this.file.takeIf { it.isFile }?.runCatching { NativeImage.read(FileInputStream(this)) }?.getOrNull()
        this.image?.let { image -> McClient.self.textureManager.register(this.location, DynamicTexture(image)) }
    }

    private fun loadDefaultTexture() {
        val default = NativeImage(16, 16, false)
        for (i in 0 until default.width) {
            for (j in 0 until default.height) {
                default.setPixel(i, j, 0)
            }
        }
        McClient.self.textureManager.register(this.location, DynamicTexture(default))
    }

    fun getId(): ResourceLocation {
        if (!this.uploaded) {
            this.loadDefaultTexture()
            this.loadFromFile()

            if (this.image == null) {
                runDownload(this.url, this.file, this::loadFromFile)
            }
            this.uploaded = true
        }
        return this.location
    }

    companion object {

        val CODEC: Codec<MapImage> = Codec.STRING.xmap(::MapImage, MapImage::url)
    }
}