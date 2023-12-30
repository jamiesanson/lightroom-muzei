// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    alias(libs.plugins.ksp)
    id("dev.sanson.android.library")
}

android {
    namespace = "dev.sanson.lightroom.sdk.android"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.browser)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work)

    implementation(libs.retrofit)

    implementation(projects.lib.lightroom)
}
