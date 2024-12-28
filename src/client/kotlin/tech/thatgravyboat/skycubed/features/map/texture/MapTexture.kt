package tech.thatgravyboat.skycubed.features.map.texture

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.platform.TextureUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.texture.SimpleTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.features.map.texture.DownloadedAsset.runDownload
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.CompletableFuture

class DownloadedTexture(
    private val file: File,
    private val url: String,
    location: ResourceLocation
) : SimpleTexture(location) {

    private var future: CompletableFuture<Void>? = null
    private var uploaded = false

    private fun loadCallback(image: NativeImage) {
        McClient.tell {
            this.uploaded = true
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall { this.upload(image) }
            } else {
                this.upload(image)
            }
        }
    }

    private fun upload(image: NativeImage) {
        TextureUtil.prepareImage(this.getId(), image.width, image.height)
        image.upload(0, 0, 0, true)
    }

    override fun load(manager: ResourceManager) {
        McClient.tell {
            if (!this.uploaded) {
                runCatching { super.load(manager) }
                this.uploaded = true
            }
        }
        if (this.future == null) {
            val nativeimage = if (file.isFile) this.load(this.file) else Optional.empty()

            if (nativeimage.isPresent) {
                this.loadCallback(nativeimage.get())
            } else {
                this.future = runDownload(this.url, file) {
                    load(file).ifPresent(this::loadCallback)
                }
            }
        }
    }

    private fun load(file: File): Optional<NativeImage> {
        return try {
            Optional.of(NativeImage.read(FileInputStream(file)))
        } catch (_: Exception) {
            Optional.empty()
        }
    }
}