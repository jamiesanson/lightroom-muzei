@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("dev.sanson.android.library")
    id("dev.sanson.android.compose")
}

android {
    namespace = "dev.sanson.lightroom.coil"
}

dependencies {
    implementation(libs.coil)
    implementation(projects.lib.lightroom)
    testImplementation(libs.bundles.testing.unit)
}
