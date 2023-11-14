plugins {
    id("dev.sanson.android.feature")
}

android {
    namespace = "dev.sanson.lightroom.ui.filter"
}

dependencies {
    implementation(projects.common.config)
    implementation(projects.lib.lightroom)
}
