import dev.sanson.lightroom.buildlogic.BuildType

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.ksp)
    id("dev.sanson.lightroom.android.application")
    id("dev.sanson.lightroom.android.hilt")
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

        @Suppress("UnstableApiUsage")
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

ksp {
    arg("circuit.codegen.mode", "hilt")
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
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.coil)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.google.fonts)
    implementation(libs.androidx.work)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.circuit)
    implementation(libs.circuit.codegen.annotations)

    implementation(projects.lightroom)

    ksp(libs.circuit.codegen)

    implementation(libs.circuitx)
    testImplementation(libs.circuit.test)

    testImplementation(libs.bundles.testing.unit)

    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
