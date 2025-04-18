package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.authlib.SignatureState
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.minecraft.MinecraftProfileTextures
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.ui.modals.Modals
import net.minecraft.Util
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import org.joml.Vector2i
import tech.thatgravyboat.lib.displays.Display
import tech.thatgravyboat.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.mixins.SkinManagerInvoker
import tech.thatgravyboat.skycubed.utils.Codecs

data class NpcPoi(
    private val texture: String,
    private val link: String,
    override val tooltip: List<Component>,
    override val position: Vector2i,
) : Poi {

    private val skin by lazy {
        val manager = McClient.self.skinManager as SkinManagerInvoker
        manager.callRegisterTextures(Util.NIL_UUID, MinecraftProfileTextures(
            MinecraftProfileTexture(this.texture, emptyMap()),
            null,
            null,
            SignatureState.SIGNED
        ))
    }

    override val id: String = "npc"
    override val bounds: Vector2i = Vector2i(10, 10)
    override val display: Display = Displays.outline({ 0xFFFFFFFFu }, Displays.face({
        if (skin.isDone) skin.get().texture else DefaultPlayerSkin.getDefaultTexture()
    }))

    override fun click() {
        Modals.action()
            .withTitle(Text.of("Open Link"))
            .withContent(Text.multiline(
                "Are you sure you want to open this link?",
                "",
                "§9${link.removePrefix("https://").removePrefix("http://")}"
            ))
            .withAction(Widgets.button {
                it.withSize(70, 20)
                it.withRenderer(WidgetRenderers.text(Text.of("Close")))
                it.withCallback {
                    McScreen.self?.onClose()
                }
            })
            .withAction(Widgets.button {
                it.withSize(70, 20)
                it.withTexture(UIConstants.PRIMARY_BUTTON)
                it.withRenderer(WidgetRenderers.text<Button?>(Text.of("Open")).withColor(MinecraftColors.WHITE))
                it.withCallback {
                    Util.getPlatform().openUri(link)
                }
            })
            .open()
    }

    companion object {

        val CODEC: MapCodec<NpcPoi> = RecordCodecBuilder.mapCodec { it.group(
            Codec.STRING.fieldOf("texture").forGetter(NpcPoi::texture),
            Codec.STRING.fieldOf("link").forGetter(NpcPoi::link),
            ComponentSerialization.CODEC.listOf().fieldOf("tooltip").forGetter(NpcPoi::tooltip),
            Codecs.vec2i("x", "z").fieldOf("position").forGetter(NpcPoi::position),
        ).apply(it, ::NpcPoi) }
    }
}