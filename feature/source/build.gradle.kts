@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("dev.sanson.android.feature")
}

android {
    namespace = "dev.sanson.lightroom.feature.source"
}

dependencies {
    implementation(projects.common.config)
}
