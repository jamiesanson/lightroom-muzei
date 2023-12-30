// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("application")
    id("dev.sanson.spotless")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass = "dev.sanson.lightroom.search.SearchLightroom"
}

val invoker: Configuration by configurations.creating

dependencies {
    compileOnly(libs.google.cloud.functions.api)
    invoker(libs.google.cloud.functions.invoker)

    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.lib.lightroom)
}

task<JavaExec>("runFunction") {
    mainClass = "com.google.cloud.functions.invoker.runner.Invoker"
    classpath(invoker)
    inputs.files(configurations.runtimeClasspath, sourceSets["main"].output)
    args(
        "--target",
        project.findProperty("runFunction.target") ?: "dev.sanson.lightroom.search.SearchLightroom",
        "--port",
        project.findProperty("runFunction.port") ?: 8080,
    )
    doFirst {
        args("--classpath", files(configurations.runtimeClasspath, sourceSets["main"].output).asPath)
    }
}

tasks.named("build") {
    dependsOn(":shadowJar")
}

task("buildFunction") {
    dependsOn("build")
    copy {
        from("build/libs/" + rootProject.name + "-all.jar")
        into("build/deploy")
    }
}
