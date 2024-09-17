package dev.sanson.buildlogic

@Suppress("ConstPropertyName")
internal object Plugins {
    const val Application = "com.android.application"
    const val Compose = "org.jetbrains.kotlin.plugin.compose"
    const val Ksp = "com.google.devtools.ksp"
    const val Hilt = "dagger.hilt.android.plugin"
    const val KtLint = "org.jlleitschuh.gradle.ktlint"
    const val Library = "com.android.library"
    const val Spotless = "com.diffplug.spotless"

    object Kotlin {
        const val Android = "org.jetbrains.kotlin.android"
        const val Serialization = "org.jetbrains.kotlin.plugin.serialization"
        const val Parcelize = "org.jetbrains.kotlin.plugin.parcelize"
    }
}