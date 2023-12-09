import dev.sanson.buildlogic.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Convention plugin allowing modules to just install spotless support
 */
class SpotlessConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configureSpotless()
    }
}