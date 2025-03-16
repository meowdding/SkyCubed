package tech.thatgravyboat.skycubed.config.rendering

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo

@ConfigInfo(titleTranslation = "config.skycubed.render.title")
@Category("render")
object RenderingConfig {

    @ConfigEntry(id = "showOwnTag", translation = "config.skycubed.render.showOwnTag")
    @Comment("", translation = "config.skycubed.render.showOwnTag.desc")
    var showOwnTag = false

}