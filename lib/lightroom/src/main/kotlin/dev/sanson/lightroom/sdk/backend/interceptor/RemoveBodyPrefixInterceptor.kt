// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

private const val MITIGATION_PREFIX = "while (1) {}\n"

/**
 * The Lightroom APIs prepend every response with the following:
 *
 * while (1) {}
 *
 * as an "abuse mitigation". This interceptor strips the first line from the response before it makes
 * its way to the deserializer.
 *
 * For more information on this ridiculous design choice, see https://github.com/AdobeDocs/lightroom-partner-apis/issues/93
 */
internal class RemoveBodyPrefixInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.body == null) return response

        val newBody =
            response.body
                ?.string()
                ?.removePrefix(MITIGATION_PREFIX)
                ?.toResponseBody()

        return response.newBuilder()
            .body(newBody)
            .build()
    }
}
