package tech.thatgravyboat.skycubed

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skycubed.api.overlays.Overlays

class SkyCubed : ModInitializer {

    override fun onInitialize() {
        Overlays
    }

    companion object {

        fun id(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath("skycubed", path)
        }
    }
}
