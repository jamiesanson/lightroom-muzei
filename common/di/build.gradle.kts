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
