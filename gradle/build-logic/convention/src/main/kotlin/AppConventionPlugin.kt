import dev.sanson.buildlogic.configureLinting
import dev.sanson.buildlogic.configureAndroidCompose
import dev.sanson.buildlogic.configureAndroid
import dev.sanson.buildlogic.configureKotlin
import com.android.build.api.dsl.ApplicationExtension
import dev.sanson.buildlogic.KotlinFeature
import dev.sanson.buildlogic.Plugins
import dev.sanson.buildlogic.configureCircuit
import dev.sanson.buildlogic.configureHilt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AppConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(Plugins.Application)
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

            extensions.configure<ApplicationExtension> {
                configureAndroid(this)
                configureAndroidCompose(this)

                configureLinting(this)

                defaultConfig.targetSdk = BuildVersions.targetSdk
            }
        }
    }
}