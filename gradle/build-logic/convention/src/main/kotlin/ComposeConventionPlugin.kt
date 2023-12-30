import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import dev.sanson.buildlogic.KotlinFeatures
import dev.sanson.buildlogic.Plugins
import dev.sanson.buildlogic.configureAndroidCompose
import dev.sanson.buildlogic.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Compose convention plugin allowing modules to optionally install Compose support
 */
class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            configureKotlin(features = KotlinFeatures(immutableCollections = true))

            if (pluginManager.hasPlugin(Plugins.Application)) {
                extensions.configure<ApplicationExtension> {
                    configureAndroidCompose(this)
                }
            } else if (pluginManager.hasPlugin(Plugins.Library)) {
                extensions.configure<LibraryExtension> {
                    configureAndroidCompose(this)
                }
            }
        }
    }
}