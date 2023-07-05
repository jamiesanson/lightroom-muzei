import dev.sanson.lightroom.buildlogic.configureLinting
import dev.sanson.lightroom.buildlogic.configureAndroidCompose
import dev.sanson.lightroom.buildlogic.configureAndroid
import dev.sanson.lightroom.buildlogic.configureKotlin
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AppConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            configureKotlin()

            extensions.configure<ApplicationExtension> {
                configureAndroid(this)
                configureAndroidCompose(this)

                configureLinting(this)

                defaultConfig.targetSdk = BuildVersions.targetSdk
            }
        }
    }
}