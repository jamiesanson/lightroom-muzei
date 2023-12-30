// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    id("dev.sanson.kotlin.library")
}

dependencies {
    implementation(libs.androidx.datastore)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    implementation(projects.core.data)
    implementation(projects.core.logging)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    testImplementation(libs.bundles.testing.unit)
}
