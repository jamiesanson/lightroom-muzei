package dev.sanson.lightroom.sdk

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.sdk.backend.auth.AuthManager
import dev.sanson.lightroom.sdk.domain.CatalogRepository
import dev.sanson.lightroom.sdk.domain.GetAlbumAssetsUseCase
import dev.sanson.lightroom.sdk.domain.GetAlbumsUseCase
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface Lightroom {
    /**
     * Flow describing whether or not a user is signed in
     */
    val isSignedIn: Flow<Boolean>

    /**
     * Launch the Lightroom sign in flow
     *
     * For more details, see the [Authorize request documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/UserAuthentication/IMS/#authorize-request)
     */
    fun signIn(context: Context)

    fun handleSignInResponse(intent: Intent)

    /**
     * Load albums for logged in user
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/getAlbums
     */
    suspend fun getAlbums(): List<Album>

    /**
     * List album assets
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/listAssetsOfAlbum
     */
    suspend fun getAlbumAssets(albumId: AlbumId): List<AssetId>

    /**
     * Convert an [AssetId] into a URL to be loaded
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Assets/operation/getAssetRendition
     */
    suspend fun AssetId.asUrl(rendition: Rendition): String
}

class DefaultLightroom(
    internal val authManager: AuthManager,
    internal val clientId: String,
    private val retrieveAlbums: GetAlbumsUseCase,
    private val retrieveAlbumAssets: GetAlbumAssetsUseCase,
    private val catalogRepository: CatalogRepository,
) : Lightroom {

    override val isSignedIn = authManager.isSignedIn

    override fun signIn(context: Context) {
        val intent = CustomTabsIntent.Builder().build()

        intent.launchUrl(context, authManager.buildAuthUri())
    }

    override fun handleSignInResponse(intent: Intent) {
        val code = intent.data?.getQueryParameter("code") ?: error("\"code\" not found in redirect")
        authManager.onAuthorized(code)
    }

    override suspend fun getAlbums(): List<Album> = retrieveAlbums()

    override suspend fun getAlbumAssets(albumId: AlbumId): List<AssetId> =
        retrieveAlbumAssets(albumId = albumId)

    override suspend fun AssetId.asUrl(rendition: Rendition): String {
        val catalogId = catalogRepository.getCatalog().id.id
        return "https://lr.adobe.io/v2/catalogs/$catalogId/assets/$id/renditions/${rendition.code}"
    }
}

internal suspend fun Lightroom.getAuthHeaders(): Map<String, String> {
    if (this !is DefaultLightroom) return emptyMap()

    val apiKey = mapOf("X-Api-Key" to clientId)
    val token = authManager.latestAccessToken.first()
        ?: return apiKey

    return apiKey + ("Authorization" to "Bearer $token")
}

@HiltViewModel
private class LightroomViewModel @Inject constructor(
    val lightroom: Lightroom,
) : ViewModel()

@Composable
fun rememberLightroom(): Lightroom {
    return hiltViewModel<LightroomViewModel>(LocalContext.current as ViewModelStoreOwner).lightroom
}
