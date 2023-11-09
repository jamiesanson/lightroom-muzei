import com.android.build.api.dsl.CommonExtension
import dev.sanson.buildlogic.KotlinFeature
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
            configureKotlin(KotlinFeature.ImmutableCollections)

            extensions.configure<CommonExtension<*, *, *, *, *>> {
                configureAndroidCompose(this)
            }
        }
    }
}