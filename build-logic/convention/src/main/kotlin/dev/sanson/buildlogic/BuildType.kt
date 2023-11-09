package dev.sanson.buildlogic

enum class BuildType(val applicationIdSuffix: String? = null) {
    Debug(".debug"),
    Release,
}