package tech.thatgravyboat.skycubed

import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skycubed.api.overlays.Overlays
import tech.thatgravyboat.skycubed.config.Config

class SkyCubed : ModInitializer {

    override fun onInitialize() {
        configurator.register(Config::class.java)

        SkyBlockAPI.eventBus.register(Overlays)
    }

    companion object {

        private val configurator = Configurator("skycubed")

        fun id(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath("skycubed", path)
        }
    }
}
