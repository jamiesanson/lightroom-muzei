// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.interceptor

import android.util.Log
import dev.sanson.lightroom.sdk.backend.auth.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

internal class LightroomAuthenticator(
    private val authManager: AuthManager,
) : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        // If we get a 401 and we've used an auth header in the previous request, try refresh tokens
        return if (response.code == 401 && response.request.headers["Authorization"] != null) {
            val credentials =
                runBlocking {
                    try {
                        authManager.refreshTokens()
                    } catch (e: Exception) {
                        Log.w("LightroomAuthenticator", "Token refresh failed", e)
                        null
                    }
                }

            credentials ?: return null

            response.request
                .newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer ${credentials.accessToken}")
                .build()
        } else {
            null
        }
    }
}
