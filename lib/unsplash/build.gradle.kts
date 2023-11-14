plugins {
    id("dev.sanson.android.library")
    id("dev.sanson.android.compose")
}

android {
    namespace = "dev.sanson.unsplash"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.coil)

    testImplementation(libs.bundles.testing.unit)
}
