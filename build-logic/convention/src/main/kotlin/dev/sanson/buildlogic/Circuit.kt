package dev.sanson.buildlogic

import com.android.build.gradle.internal.utils.isKspPluginApplied
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCircuit(useHilt: Boolean = true) {
    val libs = versionCatalog

    with(pluginManager) {
        if (!isKspPluginApplied(this@configureCircuit)) {
            apply(Plugins.Ksp)
        }
    }

    if (isKspPluginApplied(this@configureCircuit) && useHilt) {
        configure<KspExtension> {
            arg("circuit.codegen.mode", "hilt")
        }
    }

    dependencies {
        "implementation"(libs["circuit"])
        "implementation"(libs["circuit-codegen-annotations"])
        "ksp"(libs["circuit-codegen"])

        "implementation"(libs["circuitx"])
        "testImplementation"(libs["circuit-test"])
    }
}