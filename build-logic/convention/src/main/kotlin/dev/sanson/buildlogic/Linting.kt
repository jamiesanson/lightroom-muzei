package dev.sanson.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal fun Project.configureLinting(androidExtension: CommonExtension<*, *, *, *, *>) {
    pluginManager.apply(Plugins.KtLint)

    extensions.configure<KtlintExtension> {
        version.set("0.48.2")
    }

    androidExtension.lint {
        disable += "EmptyNavDeepLink"
    }

    val libs = versionCatalog

    dependencies {
        "ktlintRuleset"(libs["ktlint-compose-rules"])
    }
}