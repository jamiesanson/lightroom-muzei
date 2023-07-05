package dev.sanson.lightroom.backend

import android.content.Context
import android.content.Intent
import android.util.Base64
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import java.security.SecureRandom
import javax.inject.Inject

private const val LIGHTROOM_CLIENT_ID = "4a1404eeb6b442278a96dab428ecbc43"

class Lightroom @Inject constructor() {

    fun signIn(context: Context) {
        val intent = CustomTabsIntent.Builder().build()

        val challengeBytes = ByteArray(64)

        SecureRandom().nextBytes(challengeBytes)

        val challenge = Base64.encodeToString(challengeBytes, Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE)

        val authUrl = "https://ims-na1.adobelogin.com/ims/authorize/v2"

        val params = mapOf(
            "scope" to "openid,lr_partner_apis,lr_partner_rendition_apis",
            "client_id" to LIGHTROOM_CLIENT_ID,
            "response_type" to "code",
            "redirect_uri" to "dev.sanson.lightroom://callback",
            "code_challenge" to challenge,
        )

        val authUri = params
            .entries
            .fold(authUrl.toUri().buildUpon()) { builder, (key, value) ->
                builder.appendQueryParameter(key, value)
            }
            .build()

        intent.launchUrl(context, authUri)
    }

    fun handleSignInResponse(intent: Intent) {
        // TODO: Store code from intent
    }
}