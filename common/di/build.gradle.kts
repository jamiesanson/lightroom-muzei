// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.android.library.core")
    id("dev.sanson.android.hilt")
}

android {
    namespace = "dev.sanson.lightroom.common.di"
}

dependencies {
    implementation(libs.dagger)
}
