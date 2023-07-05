package dev.sanson.lightroom.buildlogic

enum class BuildType(val applicationIdSuffix: String? = null) {
    Debug(".debug"),
    Release,
}