import dev.sanson.buildlogic.configureRepositoriesForGithubPackages

plugins {
    id("dev.sanson.android.library.core")
}

configureRepositoriesForGithubPackages()

android {
    namespace = "dev.sanson.lightroom.screens"
}

dependencies {
    implementation(libs.circuit)
    implementation(libs.circuitx)
}
