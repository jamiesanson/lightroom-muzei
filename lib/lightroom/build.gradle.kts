@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.ksp)
    id("dev.sanson.android.library")
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

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(projects.core.data)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    testImplementation(libs.bundles.testing.unit)
}
