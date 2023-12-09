// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.android.library.core")
}

android {
    namespace = "dev.sanson.lightroom.screens"
}

dependencies {
    implementation(libs.circuit)
    implementation(libs.circuitx)
}
