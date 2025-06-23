package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.authlib.SignatureState
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.minecraft.MinecraftProfileTextures
import com.mojang.serialization.MapCodec
import earth.terrarium.olympus.client.ui.modals.Modals
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.skycubed.generated.SkyCubedCodecs
import net.minecraft.Util
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import org.joml.Vector3i
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.mixins.SkinManagerInvoker
import tech.thatgravyboat.skycubed.utils.CachedValue
import kotlin.time.Duration.Companion.seconds

@GenerateCodec
data class NpcPoi(
    var texture: String,
    @FieldName("link") var actualLink: String,
    var name: String = "",
    @FieldName("tooltip") var stringTooltip: MutableList<String>,
    override var position: Vector3i,
) : Poi {
    private val skin by lazy {
        runCatching {
            val manager = McClient.self.skinManager as SkinManagerInvoker
            manager.callRegisterTextures(
                Util.NIL_UUID,
                MinecraftProfileTextures(
                    MinecraftProfileTexture(this.texture, emptyMap()),
                    null,
                    null,
                    SignatureState.SIGNED,
                ),
            )
        }.getOrNull()
    }

    val link get() = actualLink.applyReplacements().stripColor().replace(" ", "_")
    override val tooltip: MutableList<Component> by CachedValue(5.seconds) {
        stringTooltip.map { Text.of(it.applyReplacements()) }.toMutableList()
    }
    override val id: String = "npc"
    override val bounds: Vector2i = Vector2i(10, 10)
    override val display: Display
        get() = runCatching {
            skin?.let {
                Displays.outline(
                    { 0xFFFFFFFFu },
                    Displays.face({ if (it.isDone) it.get().texture else DefaultPlayerSkin.getDefaultTexture() }),
                )
            }
        }.getOrNull() ?: ExtraDisplays.missingTextureDisplay()

    override fun click() {
        Modals.link(link).open()
    }

    fun String.applyReplacements(): String {
        return this.replace("\$name", name)
    }

    companion object {
        val CODEC: MapCodec<NpcPoi> = SkyCubedCodecs.NpcPoiCodec
    }
}
