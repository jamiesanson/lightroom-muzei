// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.auth

import androidx.annotation.RestrictTo
import dev.sanson.lightroom.sdk.backend.LightroomClientId
import dev.sanson.lightroom.sdk.backend.auth.api.LightroomAuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import java.security.SecureRandom
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class AuthManager
    @Inject
    constructor(
        private val applicationScope: CoroutineScope,
        private val credentialStore: CredentialStore,
        private val lightroomAuthService: LightroomAuthService,
        @LoginHost
        private val loginHost: String,
        @LightroomClientId
        private val clientId: String,
    ) {
        private var previousChallenge: String? = null

        val hasCredentials = credentialStore.credential.map { it != null }

        val latestAccessToken = credentialStore.credential.map { it?.accessToken }

        @OptIn(ExperimentalEncodingApi::class)
        fun buildAuthUri(): String {
            val challengeBytes = ByteArray(64)

            SecureRandom().nextBytes(challengeBytes)

            val challenge =
                Base64.UrlSafe.encode(challengeBytes).dropLastWhile { it == '=' }

            previousChallenge = challenge

            val authUrl = "$loginHost/ims/authorize/v2"

            val params =
                mapOf(
                    "scope" to "openid,lr_partner_apis,lr_partner_rendition_apis,offline_access",
                    "client_id" to clientId,
                    "response_type" to "code",
                    "redirect_uri" to "dev.sanson.lightroom://callback",
                    "code_challenge" to challenge,
                )

            fun String.urlEncode(): String = URLEncoder.encode(this, "UTF-8")

            return buildString {
                append(authUrl)
                append("?")

                for ((key, value) in params) {
                    append(key.urlEncode())
                    append("=")
                    append(value.urlEncode())
                    append("&")
                }

                removeSuffix("&")
            }
        }

        fun onAuthorized(code: String) {
            applicationScope.launch(Dispatchers.IO) {
                val authorization =
                    "code=$code&grant_type=authorization_code&code_verifier=$previousChallenge".toRequestBody()

                val response =
                    lightroomAuthService.fetchToken(
                        body = authorization,
                        clientId = clientId,
                    )

                credentialStore.updateTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                )
            }
        }

        suspend fun refreshTokens(): Credential =
            withContext(Dispatchers.IO) {
                val existingCredential =
                    requireNotNull(credentialStore.credential.firstOrNull()) {
                        "No existing credentials found"
                    }

                val authorization =
                    "grant_type=refresh_token&refresh_token=${existingCredential.refreshToken}".toRequestBody()

                val response =
                    lightroomAuthService.fetchToken(
                        body = authorization,
                        clientId = clientId,
                    )

                credentialStore.updateTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                )

                return@withContext requireNotNull(credentialStore.credential.first())
            }
    }
