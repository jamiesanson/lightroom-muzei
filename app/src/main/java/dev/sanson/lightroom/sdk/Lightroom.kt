package dev.sanson.lightroom.sdk

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import dev.sanson.lightroom.sdk.backend.auth.AuthManager
import dev.sanson.lightroom.sdk.domain.GetAlbumsUseCase
import dev.sanson.lightroom.sdk.model.Album
import javax.inject.Inject

class Lightroom @Inject constructor(
    private val authManager: AuthManager,
    private val retrieveAlbums: GetAlbumsUseCase,
) {

    /**
     * Flow describing whether or not a user is signed in
     */
    val isSignedIn = authManager.isSignedIn

    /**
     * Launch the Lightroom sign in flow
     *
     * For more details, see the [Authorize request documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/UserAuthentication/IMS/#authorize-request)
     */
    fun signIn(context: Context) {
        val intent = CustomTabsIntent.Builder().build()

        intent.launchUrl(context, authManager.buildAuthUri())
    }

    fun handleSignInResponse(intent: Intent) {
        val code = intent.data?.getQueryParameter("code") ?: error("\"code\" not found in redirect")
        authManager.onAuthorized(code)
    }

    /**
     * Load albums for logged in user
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/getAlbums
     */
    suspend fun getAlbums(): List<Album> = retrieveAlbums()
}
