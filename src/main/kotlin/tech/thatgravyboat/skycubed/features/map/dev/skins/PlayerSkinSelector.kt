package tech.thatgravyboat.skycubed.features.map.dev.skins

import com.google.gson.JsonParser
import me.owdding.patches.utils.getPath
import net.minecraft.world.entity.player.Player
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object PlayerSkinSelector : SkinSelector<Player> {
    @OptIn(ExperimentalEncodingApi::class)
    override fun getSkin(entity: Player): String {
        return entity.gameProfile.properties.get("textures").first().value().let {
            JsonParser.parseString(Base64.decode(it).decodeToString()).getPath("textures.SKIN.url")
                .asString("Unable to find skin :(((")
        }
    }
}
