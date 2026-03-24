plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") apply false
    id("net.fabricmc.fabric-loom") apply false
}
stonecutter active "26.1"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"

    replacements.regex {
        direction = eval(current.version, "< 1.21.11")
        replace("import net.minecraft.resources.Identifier(?!;)", "import net.minecraft.resources.ResourceLocation as Identifier")
        reverse("import net.minecraft.resources.ResourceLocation as Identifier", "import net.minecraft.resources.Identifier")
    }

    replacements.string {
        direction = eval(current.version, "< 1.21.11")
        from = "net.minecraft.util.Util"
        to = "net.minecraft.Util"
    }

    replacements.string {
        direction = eval(current.version, "< 26.1")
        replace(
            "import net.fabricmc.fabric.api.client.command.v2.ClientCommands",
            "import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager as ClientCommands",
        )
        replace(
            "import net.minecraft.client.multiplayer.chat.GuiMessage",
            "import net.minecraft.client.GuiMessage",
        )
        replace(
            "import net.minecraft.client.multiplayer.chat.GuiMessageTag",
            "import net.minecraft.client.GuiMessageTag",
        )
        replace(
            "import net.minecraft.client.renderer.state.gui.BlitRenderState",
            "import net.minecraft.client.gui.render.state.BlitRenderState"
        )
        replace(
            "import net.minecraft.client.renderer.state.gui.GuiElementRenderState",
            "import net.minecraft.client.gui.render.state.GuiElementRenderState"
        )
        replace("Lnet/minecraft/client/multiplayer/chat/GuiMessage", "Lnet/minecraft/client/GuiMessage")
        replace("Lnet/minecraft/client/multiplayer/chat/GuiMessageTag", "Lnet/minecraft/client/GuiMessageTag")

        replace(
            "import net.minecraft.client.gui.components.PlayerFaceExtractor",
            "import net.minecraft.client.gui.components.PlayerFaceRenderer"
        )
        replace(
            "PlayerFaceExtractor.extractRenderState",
            "PlayerFaceRenderer.draw"
        )
    }

    replacements.regex {
        direction = eval(current.version, "< 26.1")
        replace(
            "import net.minecraft.client.gui.GuiGraphicsExtractor(?!;)", "import net.minecraft.client.gui.GuiGraphics as GuiGraphicsExtractor",
            "import net.minecraft.client.gui.GuiGraphics as GuiGraphicsExtractor", "import net.minecraft.client.gui.GuiGraphicsExtractor"
        )
    }
}
