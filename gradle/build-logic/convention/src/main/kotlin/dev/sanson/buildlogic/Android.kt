package dev.sanson.buildlogic

import BuildVersions
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val libs = versionCatalog

    commonExtension.apply {
        compileSdk = BuildVersions.compileSdk

        defaultConfig {
            minSdk = BuildVersions.minSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            isCoreLibraryDesugaringEnabled = true
        }

        sourceSets {
            getAt("main").java.srcDirs("src/main/kotlin")
            getAt("test").java.srcDirs("src/test/kotlin")
            getAt("androidTest").java.srcDirs("src/androidTest/kotlin")
        }
    }

    dependencies {
        "coreLibraryDesugaring"(libs["android-desugar-jdk-libs"])
    }
}