plugins {
    id("dev.sanson.android.feature")
}

android {
    namespace = "dev.sanson.lightroom.ui.album"
}

dependencies {
    implementation(projects.common.config)

    implementation(projects.lib.lightroom)
    implementation(projects.lib.lightroomCoil)

    implementation(libs.coil)
}
