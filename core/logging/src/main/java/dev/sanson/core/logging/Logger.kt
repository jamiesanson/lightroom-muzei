// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.core.logging

interface Logger {
    enum class Priority {
        Verbose,
        Debug,
        Info,
        Warn,
        Error,
        Assert,
        ;

        val priorityInt = ordinal + 2
    }

    fun log(
        priority: Priority,
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )
}
