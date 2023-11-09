@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("dev.sanson.android.feature")
}

android {
    namespace = "dev.sanson.lightroom.feature.filter"
}

dependencies {
    implementation(projects.common.config)
    implementation(projects.lib.lightroom)
}
