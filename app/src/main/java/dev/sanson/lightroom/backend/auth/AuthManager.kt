package dev.sanson.lightroom.backend.auth

import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.sanson.lightroom.backend.LightroomClientId
import dev.sanson.lightroom.backend.auth.api.LightroomAuthService
import dev.sanson.lightroom.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthManager @Inject constructor(
    @ApplicationScope
    private val applicationScope: CoroutineScope,
    private val credentialStore: CredentialStore,
    private val lightroomAuthService: LightroomAuthService,
    @LoginHost
    private val loginHost: String,
    @LightroomClientId
    private val clientId: String,
) {
    // TODO: This should be using saved state
    private var previousChallenge: String? = null

    val isSignedIn = credentialStore.credential.map { it != null }

    fun buildAuthUri(): Uri {
        val challengeBytes = ByteArray(64)

        SecureRandom().nextBytes(challengeBytes)

        val challenge = Base64.encodeToString(
            challengeBytes,
            Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE
        )

        previousChallenge = challenge

        val authUrl = "$loginHost/ims/authorize/v2"

        val params = mapOf(
            "scope" to "openid,lr_partner_apis,lr_partner_rendition_apis,offline_access",
            "client_id" to clientId,
            "response_type" to "code",
            "redirect_uri" to "dev.sanson.lightroom://callback",
            "code_challenge" to challenge,
        )

        return params
            .entries
            .fold(authUrl.toUri().buildUpon()) { builder, (key, value) ->
                builder.appendQueryParameter(key, value)
            }
            .build()
    }

    fun onAuthorized(code: String) {
        applicationScope.launch(Dispatchers.IO) {
            val authorization = "code=$code&grant_type=authorization_code&code_verifier=$previousChallenge".toRequestBody()

            val response = lightroomAuthService.fetchToken(
                body = authorization,
                clientId = clientId,
            )

            credentialStore.updateTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }
    }

    suspend fun refreshTokens(): Credential = withContext(Dispatchers.IO) {
        val existingCredential = requireNotNull(credentialStore.credential.firstOrNull()) {
            "No existing credentials found"
        }

        val authorization = "grant_type=refresh_token&refresh_token=${existingCredential.refreshToken}".toRequestBody()

        val response = lightroomAuthService.fetchToken(
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