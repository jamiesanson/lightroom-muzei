plugins {
    id("dev.sanson.android.library")
    id("dev.sanson.android.hilt")
}

android {
    namespace = "dev.sanson.lightroom.muzei"
}

dependencies {
    implementation(libs.androidx.work)
    implementation(libs.coil)
    implementation(libs.muzei.api)
    implementation(libs.androidx.datastore)

    implementation(projects.common.config)
    implementation(projects.lib.lightroom)
    implementation(projects.lib.lightroomCoil)

    testImplementation(libs.bundles.testing.unit)
}
