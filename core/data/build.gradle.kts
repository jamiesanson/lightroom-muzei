plugins {
    id("dev.sanson.android.library.core")
}

android {
    namespace = "dev.sanson.lightroom.core.data"
}

dependencies {
    implementation(libs.androidx.datastore)
}
