package tech.thatgravyboat.skycubed.config.screens

import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject
import com.teamresourceful.resourcefulconfig.api.types.info.Translatable

@ConfigObject
class WardrobeConfig : Translatable {

    @ConfigEntry(id = "enabled", translation = "config.skycubed.screens.wardrobe.enabled")
    @Comment("", translation = "config.skycubed.screens.wardrobe.enabled.desc")
    var enabled = true

    @ConfigEntry(id = "textures", translation = "config.skycubed.screens.wardrobe.textures")
    @Comment("", translation = "config.skycubed.screens.wardrobe.textures.desc")
    var textured = false

    override fun getTranslationKey(): String = "Edit Wardrobe Config"
}