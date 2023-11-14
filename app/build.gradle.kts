import dev.sanson.buildlogic.BuildType

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

    implementation(libs.coil)

    implementation(libs.androidx.work)

    implementation(libs.material)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(projects.lib.lightroom)
    implementation(projects.lib.lightroomCoil)
    implementation(projects.lib.muzei)
    implementation(projects.lib.unsplash)

    implementation(projects.common.screens)
    implementation(projects.common.di)
    implementation(projects.common.ui)
    implementation(projects.common.config)

    implementation(projects.ui.album)
    implementation(projects.ui.confirm)
    implementation(projects.ui.filter)
    implementation(projects.ui.signin)
    implementation(projects.ui.source)

    testImplementation(libs.bundles.testing.unit)
}

if (file("google-services.json").exists()) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
    apply(plugin = libs.plugins.firebase.crashlytics.get().pluginId)
}
