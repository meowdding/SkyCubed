package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigColor
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigColorValue
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigInfo
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import net.fabricmc.loader.api.FabricLoader

class ConfigInfoProvider : ResourcefulConfigInfo {

    private val self = FabricLoader.getInstance().getModContainer("skycubed").get()

    override fun title(): TranslatableValue = TranslatableValue("SkyCubed (v${self.metadata.version.friendlyString})")
    override fun description(): TranslatableValue = TranslatableValue(self.metadata.description)

    override fun links(): Array<ResourcefulConfigLink> = arrayOf(
        ResourcefulConfigLink.create(
            "https://modrinth.com/project/skycubed",
            "modrinth",
            TranslatableValue("Modrinth", "config.info.skycubed.modrinth")
        ),
        ResourcefulConfigLink.create(
            "https://github.com/ThatGravyBoat/SkyCubed",
            "code",
            TranslatableValue("GitHub", "config.info.skycubed.github")
        )
    )

    override fun icon(): String = "box"
    override fun color(): ResourcefulConfigColor = ResourcefulConfigColorValue.create("#FFFFFF")
    override fun isHidden(): Boolean = false
}