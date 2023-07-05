package dev.sanson.lightroom.backend

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import dev.sanson.lightroom.backend.auth.AuthManager
import javax.inject.Inject


class Lightroom @Inject constructor(
    private val authManager: AuthManager
) {

    /**
     * Launch the Lightroom sign in flow
     *
     * For more details, see the [Authorize request documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/UserAuthentication/IMS/#authorize-request)
     */
    fun signIn(context: Context) {
        val intent = CustomTabsIntent.Builder().build()

        intent.launchUrl(context, authManager.authUri)
    }

    fun handleSignInResponse(intent: Intent) {
        val code = intent.data?.getQueryParameter("code") ?: error("\"code\" not found in redirect")
        authManager.onAuthorized(code)
    }
}