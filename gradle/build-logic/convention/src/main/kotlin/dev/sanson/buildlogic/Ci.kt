package dev.sanson.buildlogic

val isCi get() = !System.getenv("CI").isNullOrEmpty()