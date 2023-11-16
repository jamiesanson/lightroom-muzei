package dev.sanson.buildlogic

import com.android.build.gradle.internal.utils.isKspPluginApplied
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureHilt() {
    with(pluginManager) {
        apply(Plugins.Hilt)

        if (!isKspPluginApplied(this@configureHilt)) {
            apply(Plugins.Ksp)
        }
    }

    val libs = versionCatalog

    dependencies {
        "implementation"(libs["hilt.android"])
        "ksp"(libs["hilt.compiler"])
        "kspAndroidTest"(libs["hilt.compiler"])

        "ksp"(libs["androidx.hilt.compiler"])
        "implementation"(libs["androidx.hilt.work"])
    }
}