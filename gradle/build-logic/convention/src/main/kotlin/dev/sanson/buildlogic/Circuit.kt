package dev.sanson.buildlogic

import com.android.build.gradle.internal.utils.isKspPluginApplied
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

// TODO(jamiesanson) - #3 - Revert once circuit hilt support lands
fun Project.configureRepositoriesForGithubPackages() {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/jamiesanson/circuit")
            if (isCi) {
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            } else {
                credentials {
                    username = project.property("gpr.user") as String
                    password = project.property("gpr.key") as String
                }
            }
        }
    }
}

internal fun Project.configureCircuit(useHilt: Boolean = true) {
    val libs = versionCatalog

    // TODO(jamiesanson) - #3 - Revert once circuit hilt support lands
    configureRepositoriesForGithubPackages()

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