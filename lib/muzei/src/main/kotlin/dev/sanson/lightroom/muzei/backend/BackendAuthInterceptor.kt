// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei.backend

import dev.sanson.lightroom.sdk.Lightroom
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for appending Lightroom API access token as a header.
 */
internal class BackendAuthInterceptor(
    private val lightroom: Lightroom,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken =
            runBlocking { lightroom.authManager.latestAccessToken.first() }
                ?: return chain.proceed(chain.request())

        val request =
            chain.request().newBuilder()
                .addHeader("X-Lightroom-Access-Token", accessToken)
                .build()

        return chain.proceed(request)
    }
}
