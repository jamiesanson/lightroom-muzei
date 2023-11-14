plugins {
    id("dev.sanson.android.library.core")
}

android {
    namespace = "dev.sanson.lightroom.screens"
}

dependencies {
    implementation(libs.circuit)
    implementation(libs.circuitx)
}
