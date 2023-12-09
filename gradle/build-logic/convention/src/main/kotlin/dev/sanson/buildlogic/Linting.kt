package dev.sanson.buildlogic

import com.android.build.api.dsl.CommonExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import org.gradle.kotlin.dsl.configure
import org.gradle.api.Project

internal fun Project.configureSpotless() {
    pluginManager.apply(Plugins.Spotless)

    extensions.configure<SpotlessExtension> {
        val libs = project.versionCatalog

        lineEndings = LineEnding.PLATFORM_NATIVE

        format("misc") {
            target("*.md", ".gitignore")
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlin {
            target("src/**/*.kt")
            ktlint()
                .customRuleSets(
                    listOf(
                        libs["ktlint-compose-rules"].get().let {
                            "${it.group}:${it.module.name}:${it.version}"
                        },
                    ),
                )
            trimTrailingWhitespace()
            endWithNewline()
            targetExclude("**/license.kt")
            licenseHeaderFile(
                rootProject.file("gradle/spotless/license.kt"),
                "(package|@file:)",
            )
        }
        kotlinGradle {
            target("*.kts")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(
                rootProject.file("gradle/spotless/license.kt"),
                "(import|plugins|buildscript|dependencies|pluginManagement|dependencyResolutionManagement|@file:)",
            )
        }
    }
}

internal fun Project.configureAndroidLinting(androidExtension: CommonExtension<*, *, *, *, *>) {
    configureSpotless()

    androidExtension.lint {
        warningsAsErrors = true
    }
}