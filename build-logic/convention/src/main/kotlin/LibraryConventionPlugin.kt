import dev.sanson.lightroom.buildlogic.configureLinting
import dev.sanson.lightroom.buildlogic.configureAndroidCompose
import dev.sanson.lightroom.buildlogic.configureAndroid
import dev.sanson.lightroom.buildlogic.configureKotlin
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class LibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            configureKotlin()

            extensions.configure<LibraryExtension> {
                configureAndroid(this)
                configureAndroidCompose(this)
                configureLinting(this)
            }

            dependencies {
                add("androidTestImplementation", kotlin("test"))
                add("testImplementation", kotlin("test"))
            }
        }
    }
}