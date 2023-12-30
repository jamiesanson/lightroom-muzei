// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.logging

import android.os.Build
import android.util.Log
import dev.sanson.core.logging.Logger

private const val MAX_LOG_LENGTH = 4000
private const val MAX_TAG_LENGTH = 23

object LightroomForMuzeiLogger : Logger {
    override fun log(
        priority: Logger.Priority,
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        // Tag length limit was removed in API 26.
        val trimmedTag =
            if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= 26) {
                tag
            } else {
                tag.substring(0, MAX_TAG_LENGTH)
            }

        val messageWithThrowable =
            buildString {
                append(message)
                if (throwable != null) {
                    append("\n")
                    append(Log.getStackTraceString(throwable))
                }
            }.take(MAX_LOG_LENGTH)

        Log.println(priority.priorityInt, trimmedTag, messageWithThrowable)
    }
}
