// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.kotlin.library")

    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
}

group = "dev.sanson.lightroom"

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    implementation(projects.core.logging)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    testImplementation(libs.bundles.testing.unit)
}
