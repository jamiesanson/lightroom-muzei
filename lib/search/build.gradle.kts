// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.kotlin.library")
    alias(libs.plugins.kotlinx.serialization)
}

group = "dev.sanson.lightroom"

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.data)
    implementation(projects.lib.lightroom)

    testImplementation(libs.kotlinx.datetime)
    testImplementation(libs.bundles.testing.unit)
}