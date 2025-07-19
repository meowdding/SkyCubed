package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.MapCodec
import earth.terrarium.olympus.client.ui.modals.Modals
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.skycubed.generated.SkyCubedCodecs
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import org.joml.Vector3i
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.utils.CachedValue
import tech.thatgravyboat.skycubed.utils.getSkin
import tech.thatgravyboat.skycubed.utils.isActuallyDone
import kotlin.time.Duration.Companion.seconds

@GenerateCodec
data class NpcPoi(
    var texture: String,
    @FieldName("link") var actualLink: String,
    var name: String = "",
    @FieldName("tooltip") var stringTooltip: MutableList<String>,
    override var position: Vector3i,
) : Poi {
    internal val skin by lazy { McClient.self.skinManager.getSkin(this.texture) }

    val link get() = actualLink.applyReplacements().stripColor().replace(" ", "_")
    override val tooltip: MutableList<Component> by CachedValue(5.seconds) {
        stringTooltip.map { Text.of(it.applyReplacements()) }.toMutableList()
    }
    override val id: String = "npc"
    override val bounds: Vector2i = Vector2i(10, 10)
    override val display: Display get() = when {
        skin.isActuallyDone -> Displays.outline({ 0xFFFFFFFFu }, Displays.face({ skin.get().texture() }))
        skin.isCompletedExceptionally -> ExtraDisplays.missingTextureDisplay()
        else -> Displays.outline({ 0xFFFFFFFFu }, Displays.face({ DefaultPlayerSkin.getDefaultTexture() }))
    }

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
