import com.android.build.api.dsl.LibraryExtension
import dev.sanson.buildlogic.KotlinFeature
import dev.sanson.buildlogic.Plugins
import dev.sanson.buildlogic.configureAndroid
import dev.sanson.buildlogic.configureKotlin
import dev.sanson.buildlogic.configureAndroidLinting
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Core libraries are barebones Android libraries, with the Android plugin, linting and basic
 * Kotlin features enabled. Everything else is to be added on a case-by-case basis.
 */
class CoreLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(Plugins.Library)
            }

            configureKotlin(
                KotlinFeature.Serialization,
                KotlinFeature.Parcelize,
                KotlinFeature.Coroutines,
            )

            extensions.configure<LibraryExtension> {
                configureAndroid(this)
                configureAndroidLinting(this)
            }
        }
    }
}