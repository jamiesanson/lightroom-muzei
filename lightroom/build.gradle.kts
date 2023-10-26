@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.ksp)
    id("dev.sanson.lightroom.android.library")
    id("dev.sanson.lightroom.android.hilt")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "dev.sanson.lightroom.sdk"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.browser)
    implementation(libs.androidx.datastore)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(libs.bundles.testing.unit)
}
