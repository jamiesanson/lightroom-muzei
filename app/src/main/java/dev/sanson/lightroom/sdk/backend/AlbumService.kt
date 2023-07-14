package dev.sanson.lightroom.sdk.backend

import dev.sanson.lightroom.sdk.backend.model.Album
import dev.sanson.lightroom.sdk.backend.model.Asset
import dev.sanson.lightroom.sdk.backend.model.Resources
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumService {

    /**
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/getAlbums
     */
    @GET("/v2/catalogs/{catalog_id}/albums")
    suspend fun getAlbums(@Path("catalog_id") catalogId: String): Resources<Album>

    /**
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Albums/operation/listAssetsOfAlbum
     */
    @GET("/v2/catalogs/{catalog_id}/albums/{album_id}/assets")
    suspend fun getAlbumAssets(
        @Path("catalog_id") catalogId: String,
        @Path("album_id") albumId: String,
    ): Resources<Asset>
}
