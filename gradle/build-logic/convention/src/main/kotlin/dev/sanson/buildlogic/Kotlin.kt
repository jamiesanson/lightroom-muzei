package dev.sanson.buildlogic

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal enum class KotlinFeature {
    Serialization, Parcelize, Coroutines, DateTime, ImmutableCollections
}

internal fun Project.configureKotlin(vararg features: KotlinFeature) {
    with(pluginManager) {
        apply(Plugins.Kotlin.Android)

        if (KotlinFeature.Serialization in features) {
            apply(Plugins.Kotlin.Serialization)
        }
        if (KotlinFeature.Parcelize in features) {
            apply(Plugins.Kotlin.Parcelize)
        }
    }

    val libs = versionCatalog

    kotlinExtension.jvmToolchain(
        jdkVersion = libs.findVersion("java-toolchain").get().requiredVersion.toInt()
    )

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
        }
    }


    dependencies {
        if (KotlinFeature.Coroutines in features) {
            "implementation"(libs["kotlinx-coroutines-core"])
            "implementation"(libs["kotlinx-coroutines-android"])
        }

        if (KotlinFeature.DateTime in features) {
            "implementation"(libs["kotlinx-datetime"])
        }

        if (KotlinFeature.Serialization in features) {
            "implementation"(libs["kotlinx-serialization-json"])
        }

        if (KotlinFeature.ImmutableCollections in features) {
            "implementation"(libs["kotlinx-collections-immutable"])
        }
    }
}