// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.kotlin.library")
    alias(libs.plugins.kotlinx.serialization)
}

group = "dev.sanson.lightroom.backend"

dependencies {
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)

    implementation("dev.sanson.lightroom:lightroom")
    implementation("dev.sanson.lightroom:search")
}
