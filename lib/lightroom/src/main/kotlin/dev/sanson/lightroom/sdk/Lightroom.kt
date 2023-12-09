// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import dev.sanson.lightroom.sdk.backend.auth.AuthManager
import dev.sanson.lightroom.sdk.di.DaggerLightroomComponent
import dev.sanson.lightroom.sdk.domain.CatalogRepository
import dev.sanson.lightroom.sdk.domain.GenerateRenditionUseCase
import dev.sanson.lightroom.sdk.domain.GetAlbumAssetsUseCase
import dev.sanson.lightroom.sdk.domain.GetAlbumsUseCase
import dev.sanson.lightroom.sdk.domain.GetCatalogAssetsUseCase
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AlbumTreeItem
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Catalog
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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
     * Load the catalog for the logged in user
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Catalogs/operation/getCatalog
     */
    suspend fun getCatalog(): Catalog

    /**
     * Load album tree for logged in user
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/getAlbums
     */
    suspend fun getAlbums(): List<AlbumTreeItem>

    /**
     * List catalog assets
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Assets/operation/getAssets
     */
    suspend fun getCatalogAssets(): List<Asset>

    /**
     * List album assets
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/listAssetsOfAlbum
     */
    suspend fun getAlbumAssets(albumId: AlbumId): List<Asset>

    /**
     * Generate a new rendition for a given [AssetId]
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Assets/operation/generateRenditions
     */
    suspend fun generateRendition(
        asset: AssetId,
        rendition: Rendition,
    )
}

/**
 * "Constructor" function for Lightroom instance. Should be called once and then cached to avoid
 * expensive initialisation.
 *
 * @param context Context instance to use within SDK
 * @param coroutineScope Coroutine scope to use for async operations
 */
fun Lightroom(
    context: Context,
    coroutineScope: CoroutineScope,
): Lightroom {
    return DaggerLightroomComponent.builder()
        .context(context)
        .coroutineScope(coroutineScope)
        .build()
        .lightroom()
}

/**
 * Retrieve auth headers for making image requests in form of a map
 *
 * @return map of header name to value
 */
suspend fun Lightroom.getImageAuthHeaders(): Map<String, String> {
    if (this !is DefaultLightroom) return emptyMap()

    val apiKey = mapOf("X-Api-Key" to clientId)
    val token =
        authManager.latestAccessToken.first()
            ?: authManager.refreshTokens().accessToken

    return apiKey + ("Authorization" to "Bearer $token")
}

internal class DefaultLightroom(
    internal val authManager: AuthManager,
    internal val clientId: String,
    private val retrieveAlbums: GetAlbumsUseCase,
    private val retrieveAlbumAssets: GetAlbumAssetsUseCase,
    private val retrieveCatalogAssets: GetCatalogAssetsUseCase,
    private val generateRenditions: GenerateRenditionUseCase,
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

    override suspend fun getCatalog(): Catalog = catalogRepository.getCatalog()

    override suspend fun getAlbums(): List<AlbumTreeItem> = retrieveAlbums()

    override suspend fun getCatalogAssets(): List<Asset> = retrieveCatalogAssets()

    override suspend fun getAlbumAssets(albumId: AlbumId): List<Asset> = retrieveAlbumAssets(albumId = albumId)

    override suspend fun generateRendition(
        asset: AssetId,
        rendition: Rendition,
    ) = generateRenditions(asset, rendition)
}
