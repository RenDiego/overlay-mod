pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.polyfrost.org/releases")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.polyfrost.loom" -> useModule("org.polyfrost:architectury-loom:${requested.version}")
            }
        }
    }
    plugins {
        kotlin("jvm") version "2.0.20"
    }
}

rootProject.name = "Overlay"
