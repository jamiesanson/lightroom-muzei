plugins {
    id("dev.sanson.android.feature")
}

android {
    namespace = "dev.sanson.lightroom.feature.source"
}

dependencies {
    implementation(projects.common.config)
}
