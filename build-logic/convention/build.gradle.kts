plugins {
    `kotlin-dsl`
}

group = "dev.sanson.lightroom.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ktlint.gradle.plugin)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
        )

        // Set JVM target to 11
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "dev.sanson.lightroom.android.application"
            implementationClass = "AppConventionPlugin"
        }

        register("androidLibrary") {
            id = "dev.sanson.lightroom.android.library"
            implementationClass = "LibraryConventionPlugin"
        }

        register("androidHilt") {
            id = "dev.sanson.lightroom.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}