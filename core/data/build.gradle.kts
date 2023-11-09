@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("dev.sanson.android.library.core")
}

android {
    namespace = "dev.sanson.lightroom.core.data"
}

dependencies {
    implementation(libs.androidx.datastore)
}
