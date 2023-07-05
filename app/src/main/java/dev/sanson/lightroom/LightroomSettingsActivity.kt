package dev.sanson.lightroom

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import java.security.SecureRandom

private const val LIGHTROOM_CLIENT_ID = "4a1404eeb6b442278a96dab428ecbc43"

class LightroomSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Button(
                    onClick = ::loginToLightroom,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    Text("Connect Lightroom Account")
                }
            }
            // Do we have credentials? If so, this is a "disconnect"

            // Otherwise, this is a "connect"
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("LightroomSettingsActivity", "Got intent: $intent")
    }

    private fun loginToLightroom() {
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

        intent.launchUrl(this, authUri)
    }
}