import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                "implementation"(libs.findLibrary("hilt.android").get())
                "ksp"(libs.findLibrary("hilt.compiler").get())
                "kspAndroidTest"(libs.findLibrary("hilt.compiler").get())

                "ksp"(libs.findLibrary("androidx.hilt.compiler").get())
                "implementation"(libs.findLibrary("androidx.hilt.work").get())
            }
        }
    }
}