// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("dev.sanson.android.feature")
}

android {
    namespace = "dev.sanson.lightroom.ui.signin"
}

dependencies {
    implementation(projects.lib.lightroom)
    implementation(projects.lib.lightroomAndroid)
    implementation(projects.lib.unsplash)
}
