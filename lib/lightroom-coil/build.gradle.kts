// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.android.library")
    id("dev.sanson.android.compose")
}

group = "dev.sanson.lightroom"

android {
    namespace = "dev.sanson.lightroom.coil"
}

dependencies {
    implementation(libs.coil)
    implementation(projects.lib.lightroom)
    testImplementation(libs.bundles.testing.unit)
}
