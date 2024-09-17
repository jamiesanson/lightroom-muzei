@file:Suppress("UnstableApiUsage")

package dev.sanson.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val libs = versionCatalog

    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        lint {
            disable += setOf(
                // Produces false-positives with assignments in collect blocks
                "ProduceStateDoesNotAssignValue",
            )
        }

        dependencies {
            val bom = libs["androidx-compose-bom"]

            "implementation"(platform(bom))
            "androidTestImplementation"(platform(bom))

            "implementation"(libs["androidx-compose-runtime"])
            "implementation"(libs["androidx-compose-ui"])
            "implementation"(libs["androidx-compose-material3"])
            "implementation"(libs["androidx-compose-google-fonts"])

            "implementation"(libs["androidx-compose-ui-tooling-preview"])
            "debugImplementation"(libs["androidx-compose-ui-tooling"])
        }
    }
}