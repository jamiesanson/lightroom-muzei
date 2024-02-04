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

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
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

include(":app")
include(":core:data")
include(":core:logging")
include(":common:screens")
include(":common:di")
include(":common:ui")
include(":common:config")
include(":ui:album")
include(":ui:confirm")
include(":ui:filter")
include(":ui:signin")
include(":ui:source")
include(":lib:lightroom")
include(":lib:lightroom-android")
include(":lib:lightroom-coil")
include(":lib:unsplash")
include(":lib:muzei")
include(":lib:search")
// include(":backend:search")
// include(":backend:search:api")

includeBuild("backend")
