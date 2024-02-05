// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei.backend

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for appending API Key to API gateway requests
 */
internal class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .addHeader("X-Api-Key", "TODO: Inject at build")
                .build()

        return chain.proceed(request)
    }
}
