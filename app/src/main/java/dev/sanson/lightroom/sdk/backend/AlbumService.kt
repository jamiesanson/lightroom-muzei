package dev.sanson.lightroom.sdk.backend

import dev.sanson.lightroom.sdk.backend.model.Album
import dev.sanson.lightroom.sdk.backend.model.Resources
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumService {

    @GET("/v2/catalogs/{catalog_id}/albums")
    suspend fun getAlbums(@Path("catalog_id") catalogId: String): Resources<Album>
}