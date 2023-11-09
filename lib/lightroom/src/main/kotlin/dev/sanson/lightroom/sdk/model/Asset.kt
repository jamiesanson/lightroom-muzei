package dev.sanson.lightroom.sdk.model

import kotlinx.datetime.Instant

data class Asset(
    val id: AssetId,
    val catalogId: CatalogId,
    val captureDate: Instant,
    val cameraBody: String,
    val lens: String,
    val iso: Int,
    val shutterSpeed: String,
    val aperture: String,
    val focalLength: String,
    val keywords: List<String>,
    val rating: Int? = null,
    val review: Flag? = null,
) {
    enum class Flag {
        Picked, Rejected
    }
}

/**
 * Convert an [Asset] into a URL to be loaded
 *
 * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Assets/operation/getAssetRendition
 */
fun Asset.asUrl(rendition: Rendition): String =
    id.asUrl(catalogId, rendition)


/**
 * Convert an [AssetId] into a URL to be loaded
 *
 * https://developer.adobe.com/lightroom/lightroom-api-docs/api/#tag/Assets/operation/getAssetRendition
 */
fun AssetId.asUrl(catalogId: CatalogId, rendition: Rendition): String =
    "https://lr.adobe.io/v2/catalogs/$catalogId/assets/$id/renditions/${rendition.code}"
