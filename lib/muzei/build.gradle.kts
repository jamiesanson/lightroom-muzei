// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.android.library")
    id("dev.sanson.android.hilt")
}

group = "dev.sanson.lightroom"

android {
    namespace = "dev.sanson.lightroom.muzei"
    defaultConfig {
        val authority = "dev.sanson.lightroom.authority"
        manifestPlaceholders += "lightroomAuthority" to authority
    }
}

dependencies {
    implementation(libs.androidx.work)
    implementation(libs.coil)
    implementation(libs.muzei.api)
    implementation(libs.androidx.datastore)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(projects.common.config)
    implementation(projects.lib.lightroom)
    implementation(projects.lib.lightroomCoil)

    implementation("dev.sanson.lightroom.backend:api")

    testImplementation(libs.bundles.testing.unit)
}
