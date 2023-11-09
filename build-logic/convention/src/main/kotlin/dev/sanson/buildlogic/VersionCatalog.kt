package dev.sanson.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal val Project.versionCatalog: VersionCatalog
    get() =
        extensions.getByType<VersionCatalogsExtension>().named("libs")

internal operator fun VersionCatalog.get(value: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(value).get()