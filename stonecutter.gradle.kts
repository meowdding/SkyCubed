plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.13-SNAPSHOT" apply false
}
stonecutter active "1.21.11"

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
}
