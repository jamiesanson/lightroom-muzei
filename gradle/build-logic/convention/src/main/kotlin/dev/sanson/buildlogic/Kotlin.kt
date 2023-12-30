package dev.sanson.buildlogic

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal data class KotlinFeatures(
    val serialization: Boolean = false,
    val parcelize: Boolean = false,
    val coroutines: Boolean = false,
    val dateTime: Boolean = false,
    val immutableCollections: Boolean = false,
)

internal fun Project.configureKotlin(android: Boolean = true, features: KotlinFeatures = KotlinFeatures()) {
    with(pluginManager) {
        if (android) {
            apply(Plugins.Kotlin.Android)
        } else {
            apply(Plugins.Kotlin.Jvm)
        }

        if (features.serialization) {
            apply(Plugins.Kotlin.Serialization)
        }
        if (features.parcelize) {
            apply(Plugins.Kotlin.Parcelize)
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Treat all Kotlin warnings as errors (enabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors?.toBooleanStrictOrNull() ?: true

            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )

            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    val libs = versionCatalog

    dependencies {
        if (features.coroutines) {
            "implementation"(libs["kotlinx-coroutines-core"])
            if (android) {
                "implementation"(libs["kotlinx-coroutines-android"])
            }
        }

        if (features.dateTime) {
            "implementation"(libs["kotlinx-datetime"])
        }

        if (features.serialization) {
            "implementation"(libs["kotlinx-serialization-json"])
        }

        if (features.immutableCollections) {
            "implementation"(libs["kotlinx-collections-immutable"])
        }
    }
}