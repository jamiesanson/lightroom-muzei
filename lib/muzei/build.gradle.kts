@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("dev.sanson.android.library")
    id("dev.sanson.android.hilt")
}

android {
    namespace = "dev.sanson.lightroom.muzei"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val authority = "dev.sanson.lightroom.authority"
        manifestPlaceholders += "lightroomAuthority" to authority
        buildConfigField("String", "LIGHTROOM_AUTHORITY", "\"$authority\"")
    }
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
