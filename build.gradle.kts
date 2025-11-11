@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    id("fabric-loom")
    `versioned-catalogues`
    kotlin("jvm") version "2.2.20"
    alias(libs.plugins.meowdding.resources)
    alias(libs.plugins.kotlin.symbol.processor)
}

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven(
        "https://maven.teamresourceful.com/repository/maven-public/",
        "earth.terrarium",
        "com.teamresourceful",
        "tech.thatgravyboat",
        "me.owdding",
        "com.terraformersmc"
    )
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    mavenCentral()
}

configurations {
    modImplementation {
        attributes.attribute(Attribute.of("earth.terrarium.cloche.modLoader", String::class.java), "fabric")
    }
}

dependencies {
    attributesSchema {
        attribute(Attribute.of("earth.terrarium.cloche.minecraftVersion", String::class.java)) {
            disambiguationRules.add(ClocheDisambiguationRule::class) {
                params(versionedCatalog.versions.getOrFallback("sbapi-mc-version", "minecraft").toString())
            }
        }
    }

    minecraft(versionedCatalog["minecraft"])
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })
    includeImplementation(libs.skyblockapi)
    includeImplementation(libs.meowdding.lib)
    includeImplementation(libs.meowdding.remote.repo)
    //includeImplementation(versionedCatalog["placeholders"])
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(versionedCatalog["fabric.api"])
    includeImplementation(versionedCatalog["resourceful.lib"])
    includeImplementation(versionedCatalog["resourceful.config"])
    includeImplementation(versionedCatalog["olympus"])
    includeImplementation(libs.resourceful.config.kotlin)
    compileOnly(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)
    ksp(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktcodecs)
}

fun DependencyHandler.includeImplementation(dep: Any) {
    include(dep)
    modImplementation(dep)
}

val mcVersion = stonecutter.current.version.replace(".", "")
loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=" + '"' + rootProject.projectDir.resolve("run/${mcVersion}Mods").absolutePath + '"')
    }

    val accessWidenerFile = project.file("skycubed.accesswidener")
    if (accessWidenerFile.exists()) {
        accessWidenerPath.set(accessWidenerFile)
    }
}

compactingResources {
    basePath = "repo"
    pathDirectory = "../../src"

    configureTask(tasks.named<AbstractCopyTask>("processResources").get())
    compactToArray("rooms")
}

ksp {
    arg("meowdding.project_name", "SkyCubed")
    arg("meowdding.package", "me.owdding.skycubed.generated")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to version,
                "minecraft" to versionedCatalog.versions["minecraft"]
            )
        )
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}
