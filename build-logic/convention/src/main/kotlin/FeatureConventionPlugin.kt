import com.android.build.api.dsl.LibraryExtension
import dev.sanson.buildlogic.KotlinFeature
import dev.sanson.buildlogic.Plugins
import dev.sanson.buildlogic.configureAndroid
import dev.sanson.buildlogic.configureAndroidCompose
import dev.sanson.buildlogic.configureCircuit
import dev.sanson.buildlogic.configureHilt
import dev.sanson.buildlogic.configureKotlin
import dev.sanson.buildlogic.configureLinting
import dev.sanson.buildlogic.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Feature libraries contain screens & feature-related code, and should be lightweight to spin up.
 * As such, this convention plugin applies everything you might need to get started with a new
 * feature, such as Compose, linting, hilt & navigation via circuit.
 */
class FeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(Plugins.Library)
            }

            configureKotlin(
                KotlinFeature.Serialization,
                KotlinFeature.Parcelize,
                KotlinFeature.Coroutines,
                KotlinFeature.DateTime,
                KotlinFeature.ImmutableCollections,
            )

            configureHilt()
            configureCircuit()

            extensions.configure<LibraryExtension> {
                configureAndroid(this)
                configureAndroidCompose(this)

                configureLinting(this)
            }

            val libs = versionCatalog
            dependencies {
                "implementation"(project(":common:ui"))
                "implementation"(project(":common:di"))
                "implementation"(project(":common:screens"))

                "testImplementation"(libs.findBundle("testing-unit").get())
            }
        }
    }
}