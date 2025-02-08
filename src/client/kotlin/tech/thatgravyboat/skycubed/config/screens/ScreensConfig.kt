package tech.thatgravyboat.skycubed.config.screens

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo

@ConfigInfo(titleTranslation = "config.skycubed.screens.title")
@Category("screens")
object ScreensConfig {

    @ConfigEntry(id = "equipment", translation = "config.skycubed.screens.equipment")
    @Comment("", translation = "config.skycubed.screens.equipment.desc")
    var equipment = true

    @ConfigEntry(id = "wardrobe", translation = "config.skycubed.screens.wardrobe")
    @Comment("", translation = "config.skycubed.screens.wardrobe.desc")
    val wardrobe = WardrobeConfig()
}