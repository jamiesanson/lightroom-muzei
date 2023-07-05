package dev.sanson.lightroom.backend.auth

import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.sanson.lightroom.backend.auth.api.LightroomAuthService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

private const val LIGHTROOM_CLIENT_ID = "4a1404eeb6b442278a96dab428ecbc43"
private const val ADOBE_LOGIN_HOST = "https://ims-na1.adobelogin.com"

@Singleton
class AuthManager @Inject constructor(
    private val credentialStore: CredentialStore,
) {
    private var previousChallenge: String? = null

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val lightroomAuthService by lazy {
        Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
            .baseUrl(ADOBE_LOGIN_HOST)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create<LightroomAuthService>()
    }

    val authUri: Uri
        get() {
            val challengeBytes = ByteArray(64)

            SecureRandom().nextBytes(challengeBytes)

            val challenge = Base64.encodeToString(
                challengeBytes,
                Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE
            )
            previousChallenge = challenge

            val authUrl = "$ADOBE_LOGIN_HOST/ims/authorize/v2"

            val params = mapOf(
                "scope" to "openid,lr_partner_apis,lr_partner_rendition_apis,offline_access",
                "client_id" to LIGHTROOM_CLIENT_ID,
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

    /**
     * TODO: Fix this coroutines usage, provide application scope
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun onAuthorized(code: String) {
        GlobalScope.launch {
            val authorization = "code=$code&grant_type=authorization_code&code_verifier=$previousChallenge".toRequestBody()

            val response = lightroomAuthService.fetchToken(
                body = authorization,
                clientId = LIGHTROOM_CLIENT_ID,
            )

            credentialStore.updateTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }
    }
}