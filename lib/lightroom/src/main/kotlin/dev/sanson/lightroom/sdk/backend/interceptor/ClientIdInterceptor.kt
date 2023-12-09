// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.interceptor

import okhttp3.Interceptor
import okhttp3.Response

internal class ClientIdInterceptor(
    private val clientId: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .addHeader("X-API-Key", clientId)
                .build()

        return chain.proceed(request)
    }
}
