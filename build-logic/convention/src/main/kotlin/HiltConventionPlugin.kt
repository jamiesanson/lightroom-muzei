import dev.sanson.buildlogic.configureHilt
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Hilt convention plugin allowing modules to optionally install Hilt support
 */
class HiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configureHilt()
    }
}