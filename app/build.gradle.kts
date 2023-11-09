import dev.sanson.buildlogic.BuildType

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("dev.sanson.android.application")
}

android {
    namespace = "dev.sanson.lightroom"

    defaultConfig {
        applicationId = "dev.sanson.lightroom"
        versionCode = libs.versions.versioncode.get().toInt()
        versionName = libs.versions.app.get()

        val authority = "dev.sanson.lightroom.authority"
        manifestPlaceholders += "lightroomAuthority" to authority
        buildConfigField("String", "LIGHTROOM_AUTHORITY", "\"$authority\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = BuildType.Debug.applicationIdSuffix
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.muzei.api)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.coil)

    implementation(libs.androidx.work)

    implementation(libs.material)

    implementation(projects.lib.lightroom)
    implementation(projects.lib.lightroomCoil)
    implementation(projects.lib.unsplash)

    implementation(projects.common.screens)
    implementation(projects.common.di)
    implementation(projects.common.ui)
    implementation(projects.common.config)

    implementation(projects.feature.album)
    implementation(projects.feature.filter)
    implementation(projects.feature.source)

    testImplementation(libs.bundles.testing.unit)
}
