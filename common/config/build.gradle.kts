plugins {
    id("dev.sanson.android.library.core")
    id("dev.sanson.android.hilt")
}

android {
    namespace = "dev.sanson.lightroom.common.config"
}

dependencies {
    implementation(libs.androidx.datastore)

    implementation(projects.common.di)
    implementation(projects.lib.lightroom)
    api(projects.core.data)

    testImplementation(libs.kotlinx.datetime)
    testImplementation(libs.bundles.testing.unit)
}
