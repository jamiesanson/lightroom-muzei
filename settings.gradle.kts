// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
pluginManagement {
    includeBuild("gradle/build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "lightroom-for-muzei"

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
    ":backend:search",
)
