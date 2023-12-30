// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.core.logging

import dev.sanson.core.logging.Logger.Priority.Debug

object Logging {
    private val loggers = mutableSetOf<Logger>()

    fun addLogger(logger: Logger) = loggers.add(logger)

    @PublishedApi
    internal fun log(
        priority: Logger.Priority,
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        loggers.forEach { it.log(priority, tag, message, throwable) }
    }
}

inline fun Any.logcat(
    priority: Logger.Priority = Debug,
    tag: String? = null,
    throwable: Throwable? = null,
    message: () -> String,
) {
    val tagOrCaller = tag ?: outerClassSimpleName()

    Logging.log(
        priority = priority,
        tag = tagOrCaller,
        throwable = throwable,
        message = message(),
    )
}

/**
 * An overload for logging that does not capture the calling code as tag. This should only
 * be used in standalone functions where there is no `this`.
 * @see logcat above
 */
inline fun logcat(
    tag: String,
    priority: Logger.Priority = Debug,
    throwable: Throwable? = null,
    message: () -> String,
) {
    Logging.log(
        priority = priority,
        tag = tag,
        throwable = throwable,
        message = message(),
    )
}

@PublishedApi
internal fun Any.outerClassSimpleName(): String {
    val javaClass = this::class.java
    val fullClassName = javaClass.name
    val outerClassName = fullClassName.substringBefore('$')
    val simplerOuterClassName = outerClassName.substringAfterLast('.')
    return if (simplerOuterClassName.isEmpty()) {
        fullClassName
    } else {
        simplerOuterClassName.removeSuffix("Kt")
    }
}
