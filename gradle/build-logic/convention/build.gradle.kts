plugins {
    `kotlin-dsl`
}

group = "dev.sanson.buildlogic"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.spotless.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
        )
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "dev.sanson.android.application"
            implementationClass = "AppConventionPlugin"
        }

        register("androidLibrary") {
            id = "dev.sanson.android.library"
            implementationClass = "LibraryConventionPlugin"
        }

        register("androidCoreLibrary") {
            id = "dev.sanson.android.library.core"
            implementationClass = "CoreLibraryConventionPlugin"
        }

        register("androidFeature") {
            id = "dev.sanson.android.feature"
            implementationClass = "FeatureConventionPlugin"
        }

        register("androidCompose") {
            id = "dev.sanson.android.compose"
            implementationClass = "ComposeConventionPlugin"
        }

        register("androidHilt") {
            id = "dev.sanson.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }

        register("spotless") {
            id = "dev.sanson.spotless"
            implementationClass = "SpotlessConventionPlugin"
        }
    }
}