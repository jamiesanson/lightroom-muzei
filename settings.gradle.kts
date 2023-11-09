@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Muzei-Lightroom"

include(
    ":app",
    ":lib:lightroom",
    ":lib:lightroom-coil",
    ":lib:unsplash",
    ":lib:muzei",
    ":common:screens",
    ":common:di",
    ":common:ui",
    ":common:config",
    ":core:data",
    ":ui:album",
    ":ui:confirm",
    ":ui:filter",
    ":ui:signin",
    ":ui:source",
)
