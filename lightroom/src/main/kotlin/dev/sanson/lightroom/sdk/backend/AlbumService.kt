package dev.sanson.lightroom.sdk.backend

import dev.sanson.lightroom.sdk.backend.model.Album
import dev.sanson.lightroom.sdk.backend.model.AssetsPageResponse
import dev.sanson.lightroom.sdk.backend.model.Resources
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AlbumService {

    /**
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/getAlbums
     */
    @GET("/v2/catalogs/{catalog_id}/albums")
    suspend fun getAlbums(@Path("catalog_id") catalogId: String): Resources<Album>

    /**
     * Note: The API requires negative times for capturedAfter & capturedBefore to support fetching
     * of assets without captured dates.
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/listAssetsOfAlbum
     */
    @GET("/v2/catalogs/{catalog_id}/albums/{album_id}/assets")
    suspend fun getAlbumAssets(
        @Path("catalog_id")
        catalogId: String,
        @Path("album_id")
        albumId: String,
        @Query("captured_after")
        capturedAfter: String? = null,
        @Query("captured_before")
        capturedBefore: String? = null,
        @Query("subtype")
        subtype: String = "image",
        @Query("embed")
        embed: String = "asset",
        @Query("hide_stacked_assets")
        hideStackedAssets: Boolean = true,
        @Query("limit")
        limit: Int = 1,
    ): AssetsPageResponse
}
