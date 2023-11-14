plugins {
    id("dev.sanson.android.feature")
}

android {
    namespace = "dev.sanson.lightroom.ui.signin"
}

dependencies {
    implementation(projects.lib.lightroom)
    implementation(projects.lib.unsplash)
}
