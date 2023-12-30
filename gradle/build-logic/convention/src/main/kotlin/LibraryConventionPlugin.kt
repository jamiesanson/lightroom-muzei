import com.android.build.api.dsl.LibraryExtension
import dev.sanson.buildlogic.configureAndroidLinting
import dev.sanson.buildlogic.configureAndroid
import dev.sanson.buildlogic.configureKotlin
import dev.sanson.buildlogic.KotlinFeatures
import dev.sanson.buildlogic.Plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Library modules are standalone Android libraries that contain significantly more logic than
 * core modules. These may act as SDKs, or even integration layers.
 */
class LibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(Plugins.Library)
            }

            configureKotlin(
                features = KotlinFeatures(
                    serialization = true,
                    parcelize = true,
                    coroutines = true,
                    dateTime = true,
                ),
            )

            extensions.configure<LibraryExtension> {
                configureAndroid(this)
                configureAndroidLinting(this)
            }
        }
    }
}