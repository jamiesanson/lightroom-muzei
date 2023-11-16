package dev.sanson.buildlogic

import java.io.File

data class Keystore(
    val file: File,
    val password: String,
    val keyAlias: String,
    val keyPassword: String,
)