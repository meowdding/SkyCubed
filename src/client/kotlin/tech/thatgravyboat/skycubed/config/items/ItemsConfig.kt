package tech.thatgravyboat.skycubed.config.items

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo

@ConfigInfo(titleTranslation = "config.skycubed.items.title")
@Category("items")
object ItemsConfig {

    @ConfigEntry(id = "itembars", translation = "config.skycubed.items.itembars")
    @Comment("", translation = "config.skycubed.items.itembars.desc")
    var itembars = true

    @ConfigEntry(id = "cooldowns", translation = "config.skycubed.items.cooldowns")
    @Comment("", translation = "config.skycubed.items.cooldowns.desc")
    var cooldowns = true

}