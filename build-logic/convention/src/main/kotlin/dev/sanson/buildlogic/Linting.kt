package dev.sanson.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal fun Project.configureLinting(androidExtension: CommonExtension<*, *, *, *, *>) {
    pluginManager.apply(Plugins.KtLint)

    extensions.configure<KtlintExtension> {
        version.set("1.0.1")
        outputToConsole.set(true)
    }

    androidExtension.lint {
        warningsAsErrors = true
    }

    val libs = versionCatalog

    tasks.getByName("lint").dependsOn("ktlintCheck")

    dependencies {
        "ktlintRuleset"(libs["ktlint-compose-rules"])
    }
}