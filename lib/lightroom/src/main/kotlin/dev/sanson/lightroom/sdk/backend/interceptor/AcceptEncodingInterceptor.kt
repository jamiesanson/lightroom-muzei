// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * The Lightroom APIs advertise serving a gzipped response, however they actually don't. This
 * interceptor appends an `Accept-Encoding: identity` header to ensure OkHttp doesn't attempt to un-gzip
 * incoming responses.
 *
 * Relevant links:
 * * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding
 * * https://github.com/square/okio/issues/299
 */
internal class AcceptEncodingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain
                .request()
                .newBuilder()
                .addHeader("Accept-Encoding", "identity")
                .build()

        return chain.proceed(request)
    }
}
