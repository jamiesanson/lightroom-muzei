import dev.sanson.buildlogic.KotlinFeatures
import dev.sanson.buildlogic.Plugins
import dev.sanson.buildlogic.configureKotlin
import dev.sanson.buildlogic.configureSpotless
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.withType

/**
 * Library modules are standalone Android libraries that contain significantly more logic than
 * core modules. These may act as SDKs, or even integration layers.
 */
class KotlinLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(Plugins.KotlinLibrary)
            }

            configureSpotless()

            configureKotlin(
                android = false,
                features = KotlinFeatures(coroutines = true),
            )

            tasks.withType<JavaCompile> {
                sourceCompatibility = JavaVersion.VERSION_17.toString()
                targetCompatibility = JavaVersion.VERSION_17.toString()
            }
        }
    }
}