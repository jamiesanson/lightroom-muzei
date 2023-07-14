package dev.sanson.lightroom.sdk.backend

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AssetsService {

    /**
     * Generate renditions for file
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Assets/operation/generateRenditions
     */
    @POST("/v2/catalogs/{catalog_id}/assets/{asset_id}/renditions")
    suspend fun generateRendition(
        @Path("catalog_id") catalogId: String,
        @Path("asset_id") assetId: String,
        @Header("X-Generate-Renditions") renditions: String,
    )
}
