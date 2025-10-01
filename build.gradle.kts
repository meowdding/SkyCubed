@file:Suppress("UnstableApiUsage")

import earth.terrarium.cloche.api.metadata.ModMetadata
import net.msrandom.minecraftcodev.core.utils.toPath
import net.msrandom.minecraftcodev.fabric.task.JarInJar
import net.msrandom.minecraftcodev.runs.task.WriteClasspathFile
import net.msrandom.stubs.GenerateStubApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.io.path.*

plugins {
    idea
    kotlin("jvm") version "2.2.0"
    alias(libs.plugins.terrarium.cloche)
    id("maven-publish")
    alias(libs.plugins.meowdding.resources)
    alias(libs.plugins.meowdding.repo)
    alias(libs.plugins.kotlin.symbol.processor)
}

base {
    archivesName.set(project.name.lowercase())
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    maven(url = "https://maven.fabricmc.net/")
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven(url = "https://maven.nucleoid.xyz")
    maven(url = "https://maven.shedaniel.me/")
    maven(url = "https://maven.msrandom.net/repository/root")
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)
    ksp(libs.meowdding.ktcodecs)

    compileOnly(libs.kotlin.stdlib)
}

cloche {
    metadata {
        modId = "skycubed"
        name = "SkyCubed"
        license = "MIT"
        icon = "assets/skycubed/icon.png"
        description =
            "SkyCubed is a Hypixel SkyBlock UI overhaul mod, changing all aspects of the UI in-game to be more inline into it looking like its own game."
        author("ThatGravyBoat")
        author("j10a1n15")
        clientOnly = true
    }

    common {
        mixins.from("src/mixins/skycubed.client.mixins.json")

        dependencies {
            compileOnly(libs.meowdding.ktcodecs)
            compileOnly(libs.meowdding.ktmodules)

            modImplementation(libs.meowdding.lib)
            modImplementation(libs.skyblockapi)
            modImplementation(libs.resourceful.config.kotlin) { isTransitive = false }
            modImplementation(libs.meowdding.patches) { isTransitive = false }

            modImplementation(libs.fabric.language.kotlin)
        }
    }

    fun createVersion(
        name: String,
        version: String = name,
        loaderVersion: Provider<String> = libs.versions.fabric.loader,
        fabricApiVersion: Provider<String> = libs.versions.fabric.api,
        minecraftVersionRange: ModMetadata.VersionRange.() -> Unit = {
            start = version
            end = version
            endExclusive = false
        },
        dependencies: MutableMap<String, Provider<MinimalExternalModuleDependency>>.() -> Unit = { },
    ) {
        val dependencies = mutableMapOf<String, Provider<MinimalExternalModuleDependency>>().apply(dependencies)
        val olympus = dependencies["olympus"]!!
        val rlib = dependencies["resourcefullib"]!!
        val rconfig = dependencies["resourcefulconfig"]!!

        fabric(name) {
            includedClient()
            minecraftVersion = version
            this.loaderVersion = loaderVersion.get()

            //include(libs.hypixelapi) - included in sbapi

            metadata {
                entrypoint("main") {
                    adapter = "kotlin"
                    value = "tech.thatgravyboat.skycubed.SkyCubed"
                }

                fun dependency(modId: String, version: Provider<String>? = null) {
                    dependency {
                        this.modId = modId
                        this.required = true
                        if (version != null) version {
                            this.start = version
                        }
                    }
                }

                dependency {
                    modId = "minecraft"
                    required = true
                    version(minecraftVersionRange)
                }
                dependency("fabric")
                dependency("fabricloader", libs.versions.fabric.loader)
                dependency("fabric-language-kotlin", libs.versions.fabric.language.kotlin)

                dependency("resourcefullib", rlib.map { it.version!! })
                dependency("olympus", olympus.map { it.version!! })
                //dependency("resourcefulconfig", rconfig.map { it.version!! })

                dependency("skyblock-api", libs.versions.skyblockapi)
                dependency("meowdding-lib", libs.versions.meowdding.lib)
                dependency("resourcefulconfigkt", libs.versions.resourceful.config.kotlin)
            }

            dependencies {
                fabricApi(fabricApiVersion, minecraftVersion)
                modImplementation(olympus) { isTransitive = false }
                modImplementation(rconfig) { isTransitive = false }
                modImplementation(rlib) { isTransitive = false }

                include(libs.skyblockapi)
                include(libs.resourceful.config.kotlin)
                include(libs.meowdding.lib)
                include(rlib)
                include(olympus)
                include(rconfig)
            }

            runs {
                client()
            }
        }
    }

    createVersion("1.21.5", fabricApiVersion = provider { "0.127.1" }) {
        this["resourcefullib"] = libs.resourceful.lib1215
        this["resourcefulconfig"] = libs.resourceful.config1215
        this["olympus"] = libs.olympus.lib1215
    }
    createVersion("1.21.8", minecraftVersionRange = {
        start = "1.21.6"
        end = "1.21.8"
        endExclusive = false
    }) {
        this["resourcefullib"] = libs.resourceful.lib1218
        this["resourcefulconfig"] = libs.resourceful.config1218
        this["olympus"] = libs.olympus.lib1218
    }
    createVersion("1.21.9", fabricApiVersion = provider { "0.133.7" }) {
        this["resourcefullib"] = libs.resourceful.lib1219
        this["resourcefulconfig"] = libs.resourceful.config1219
        this["olympus"] = libs.olympus.lib1219
    }

    mappings { official() }
}

compactingResources {
    basePath = "repo"
}

repo {
    sacks { includeAll() }
}

afterEvaluate {
    tasks.withType<GenerateStubApi> {
        excludes.addAll(
            "org.jetbrains.kotlin",
            "me.owdding",
            "net.hypixel",
            "maven.modrinth",
            "com.fasterxml.jackson",
            "com.google",
            "com.ibm",
            "io.netty",
            "net.fabricmc:fabric-language-kotlin",
            "com.mojang:datafixerupper",
            "com.mojang:brigardier",
            "io.github.llamalad7:mixinextras",
            "net.minidev",
            "com.nimbusds",
            "tech.thatgravyboat",
            "net.msrandom",
            "eu.pb4"
        )
    }
}

tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    filesMatching(listOf("**/*.fsh", "**/*.vsh")) {
        filter { if (it.startsWith("//!moj_import")) "#${it.substring(3)}" else it }
    }

    with(copySpec {
        from("src/lang").include("*.json").into("assets/skycubed/lang")
    })
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_2
        freeCompilerArgs.addAll(
            "-Xmulti-platform",
            "-Xno-check-actual",
            "-Xexpect-actual-classes",
            "-Xopt-in=kotlin.time.ExperimentalTime",
        )
    }
}

ksp {
    this@ksp.excludedSources.from(sourceSets.getByName("1215").kotlin.srcDirs)
    this@ksp.excludedSources.from(sourceSets.getByName("1218").kotlin.srcDirs)
    this@ksp.excludedSources.from(sourceSets.getByName("1219").kotlin.srcDirs)
    arg("meowdding.modules.project_name", project.name)
    arg("meowdding.modules.package", "me.owdding.skycubed.generated")
    arg("meowdding.codecs.project_name", project.name)
    arg("meowdding.codecs.package", "me.owdding.skycubed.generated")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}

// TODO temporary workaround for a cloche issue on certain systems, remove once fixed
tasks.withType<WriteClasspathFile>().configureEach {
    actions.clear()
    actions.add {
        output.get().toPath().also { it.parent.createDirectories() }.takeUnless { it.exists() }?.createFile()
        generate()
        val file = output.get().toPath()
        file.writeText(file.readText().lines().joinToString(File.pathSeparator))
    }
}

tasks.register("release") {
    group = "meowdding"
    sourceSets.filterNot { it.name == SourceSet.MAIN_SOURCE_SET_NAME || it.name == SourceSet.TEST_SOURCE_SET_NAME }
        .forEach {
            tasks.findByName("${it.name}JarInJar")?.let { task ->
                dependsOn(task)
                mustRunAfter(task)
            }
        }
}

tasks.getByName("build") {
    actions.clear()
    dependsOn.clear()
    dependsOn(tasks.named("release"))
}

tasks.register("cleanRelease") {
    group = "meowdding"
    listOf("clean", "release").forEach {
        tasks.getByName(it).let { task ->
            dependsOn(task)
            mustRunAfter(task)
        }
    }
}

tasks.withType<JarInJar>().configureEach {
    include { !it.name.endsWith("-dev.jar") }
    archiveBaseName = "SkyCubed"

    manifest {
        attributes["Fabric-Loom-Mixin-Remap-Type"] = "static"
        attributes["Fabric-Jar-Type"] = "classes"
        attributes["Fabric-Mapping-Namespace"] = "intermediary"
    }
}

