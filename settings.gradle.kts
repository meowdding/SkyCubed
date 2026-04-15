pluginManagement {
    repositories {
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
        maven("https://maven.teamresourceful.com/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("dev.kikugie.stonecutter") version "0.9"
}
rootProject.name = "skycubed"

val versions = listOf("26.1", "1.21.11", "1.21.10")

stonecutter {
    create(rootProject) {
        versions(versions)
        versions.forEach {
            version(it).buildscript = if (stonecutter.eval(it, "<=1.21.11")) "build.obf.gradle.kts" else "build.gradle.kts"
        }
        vcsVersion = versions.first()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        versions.forEach {
            val name = it.replace(".", "")
            create("libs$name") {
                from(
                    files(
                        rootProject.projectDir.resolve("gradle/${it.replace(".", "_")}.versions.toml")
                    )
                )
            }
        }
    }
}
