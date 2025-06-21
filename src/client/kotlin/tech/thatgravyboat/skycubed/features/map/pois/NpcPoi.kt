package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.authlib.SignatureState
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.minecraft.MinecraftProfileTextures
import com.mojang.serialization.MapCodec
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.ui.UIConstants
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
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.mixins.SkinManagerInvoker
import tech.thatgravyboat.skycubed.utils.CachedValue
import kotlin.time.Duration.Companion.seconds

@GenerateCodec
data class NpcPoi(
    var texture: String,
    @FieldName("link") var actualLink: String,
    var name: String = "",
    @FieldName("tooltip") var stringTooltip: MutableList<String>,
    override var position: Vector2i,
) : Poi {
    private val skin by lazy {
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
    }

    val link get() = actualLink.applyReplacements().stripColor().replace(" ", "_")
    override val tooltip: MutableList<Component> by CachedValue(5.seconds) {
        stringTooltip.map { Text.of(it.applyReplacements()) }.toMutableList()
    }
    override val id: String = "npc"
    override val bounds: Vector2i = Vector2i(10, 10)
    override val display: Display
        get() {
            try {
                return Displays.outline(
                    { 0xFFFFFFFFu },
                    Displays.face(
                        {
                            if (skin.isDone) skin.get().texture else DefaultPlayerSkin.getDefaultTexture()
                        },
                    ),
                )
            } catch (_: Exception) {
                fun filledDisplay(color: UInt) = Displays.background(color, Displays.empty(4, 4))
                return Displays.outline(
                    { 0xFFFFFFFFu },
                    Displays.column(
                        Displays.row(filledDisplay(0xFFFF00FFu), filledDisplay(0xFF000000u)),
                        Displays.row(filledDisplay(0xFF000000u), filledDisplay(0xFFFF00FFu)),
                    ),
                )
            }
        }

    override fun click() {
        Modals.action()
            .withTitle(Text.of("Open Link"))
            .withContent(
                Text.multiline(
                    "Are you sure you want to open this link?",
                    "",
                    "§9${link.removePrefix("https://").removePrefix("http://")}",
                ),
            )
            .withAction(
                Widgets.button {
                    it.withSize(70, 20)
                    it.withRenderer(WidgetRenderers.text(Text.of("Close")))
                    it.withCallback {
                        McScreen.self?.onClose()
                    }
                },
            )
            .withAction(
                Widgets.button {
                    it.withSize(70, 20)
                    it.withTexture(UIConstants.PRIMARY_BUTTON)
                    it.withRenderer(WidgetRenderers.text<Button?>(Text.of("Open")).withColor(MinecraftColors.WHITE))
                    it.withCallback {
                        Util.getPlatform().openUri(link)
                    }
                },
            )
            .open()
    }

    fun String.applyReplacements(): String {
        return this.replace("\$name", name)
    }

    companion object {
        val CODEC: MapCodec<NpcPoi> = SkyCubedCodecs.NpcPoiCodec
    }
}
