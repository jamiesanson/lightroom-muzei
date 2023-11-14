package dev.sanson.buildlogic

import java.io.File
import java.util.Properties

fun File.asProperties(): Properties =
    Properties().apply { load(inputStream()) }

fun Properties.requireString(key: String): String =
    requireNotNull(getProperty(key)) { "No value found for key $key" }
