plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") apply false
}
stonecutter active "26.3"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
}
