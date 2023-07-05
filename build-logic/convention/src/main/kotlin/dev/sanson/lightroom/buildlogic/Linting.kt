package dev.sanson.lightroom.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jlleitschuh.gradle.ktlint.KtlintExtension


@Suppress("UnstableApiUsage")
internal fun Project.configureLinting(androidExtension: CommonExtension<*, *, *, *>) {
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")

    extensions.configure<KtlintExtension> {
        version.set("0.48.2")
    }

    androidExtension.lint {
        disable += "EmptyNavDeepLink"
    }

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    dependencies {
        "ktlintRuleset"(libs.findLibrary("ktlint-compose-rules").get())
    }
}