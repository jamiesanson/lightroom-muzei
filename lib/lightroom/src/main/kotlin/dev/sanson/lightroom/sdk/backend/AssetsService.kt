package dev.sanson.lightroom.sdk.backend

import dev.sanson.lightroom.sdk.backend.model.AssetsPageResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AssetsService {
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

    /**
     * Retrieve catalog assets
     *
     * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Assets/operation/getAssets
     */
    @GET("/v2/catalogs/{catalog_id}/assets")
    suspend fun retrieveAssets(
        @Path("catalog_id")
        catalogId: String,
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
