package dev.sanson.buildlogic

@Suppress("ConstPropertyName")
internal object Plugins {
    const val Application = "com.android.application"
    const val Library = "com.android.library"
    const val KotlinLibrary = "java-library"
    const val Ksp = "com.google.devtools.ksp"
    const val Hilt = "dagger.hilt.android.plugin"
    const val Spotless = "com.diffplug.spotless"

    object Kotlin {
        const val Android = "org.jetbrains.kotlin.android"
        const val Jvm = "org.jetbrains.kotlin.jvm"
        const val Serialization = "org.jetbrains.kotlin.plugin.serialization"
        const val Parcelize = "org.jetbrains.kotlin.plugin.parcelize"
    }
}