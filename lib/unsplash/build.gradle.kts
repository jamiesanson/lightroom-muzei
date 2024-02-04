// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.android.library")
    id("dev.sanson.android.compose")
}

group = "dev.sanson.lightroom"

android {
    namespace = "dev.sanson.unsplash"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.coil)

    testImplementation(libs.bundles.testing.unit)
}
