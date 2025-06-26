import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm") version "2.0.20"
    alias(libs.plugins.loom)
    id("maven-publish")
    alias(libs.plugins.meowdding.resources)
    alias(libs.plugins.meowdding.repo)
    alias(libs.plugins.ksp)
}

ksp {
    arg("meowdding.modules.project_name", project.name)
    arg("meowdding.modules.package", "me.owdding.skycubed.generated")
    arg("meowdding.codecs.project_name", project.name)
    arg("meowdding.codecs.package", "me.owdding.skycubed.generated")
}

base {
    archivesName.set(project.name.lowercase())
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    runs {
        getByName("client") {
            programArg("--quickPlayMultiplayer=hypixel.net")
            vmArg("-Ddevauth.enabled=true")
            vmArg("-Dskyblockapi.debug=true")
        }
    }

    mods {
        register("skycubed") {
            sourceSet("client")
            sourceSet("main")
        }
    }

    afterEvaluate {
        val mixinPath = configurations.compileClasspath.get()
            .files { it.group == "net.fabricmc" && it.name == "sponge-mixin" }
            .first()
        runConfigs {
            "client" {
                vmArgs.add("-javaagent:$mixinPath")
            }
        }
    }
}

repositories {
    maven(url = "https://nexus.resourcefulbees.com/repository/maven-public/")
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven(url = "https://maven.nucleoid.xyz")
    maven(url = "https://maven.shedaniel.me/")
    mavenLocal()
}

dependencies {
    compileOnly(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)
    ksp(libs.meowdding.ktcodecs)

    minecraft(libs.minecraft)
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.3:2024.12.07@zip")
    })
    modImplementation(libs.loader)
    modImplementation(libs.fabrickotlin)
    modImplementation(libs.fabric)

    modImplementation(libs.hypixelapi)
    modImplementation(libs.skyblockapi)
    modImplementation(libs.rconfig)
    modImplementation(libs.rconfigkt) { isTransitive = false }
    modImplementation(libs.rlib)
    modImplementation(libs.olympus)
    modImplementation(libs.meowdding.patches)
    modImplementation(libs.meowdding.lib)

    modImplementation(libs.rei) { isTransitive = false }

    include(libs.hypixelapi)
    include(libs.skyblockapi)
    include(libs.rconfig)
    include(libs.rconfigkt) { isTransitive = false }
    include(libs.rlib)
    include(libs.olympus)
    include(libs.meowdding.patches)
    include(libs.meowdding.lib)

    modRuntimeOnly(libs.devauth)
    modRuntimeOnly(libs.modmenu)
}

compactingResources {
    basePath = "repo"
}

repo {
    sacks { includeAll() }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    filesMatching(listOf("fabric.mod.json")) {
        expand("version" to project.version)
    }
    with(copySpec {
        from("src/client/lang").include("*.json").into("assets/skycubed/lang")
    })
}

tasks.withType<ProcessResources>().configureEach { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}
