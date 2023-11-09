@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("dev.sanson.android.library.core")
}

android {
    namespace = "dev.sanson.lightroom.common.config"
}

dependencies {
    implementation(libs.androidx.datastore)

    implementation(projects.lib.lightroom)
    api(projects.core.data)
}
