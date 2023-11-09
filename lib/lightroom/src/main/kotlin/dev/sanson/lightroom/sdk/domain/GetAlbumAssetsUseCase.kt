package dev.sanson.lightroom.sdk.domain

import android.net.Uri
import dev.sanson.lightroom.sdk.backend.AlbumService
import dev.sanson.lightroom.sdk.backend.model.Href
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.AssetId
import javax.inject.Inject
import dev.sanson.lightroom.sdk.backend.model.Asset as BackendAsset

/**
 * Extension taken from androidx-core to avoid dependency on the entire library
 */
private fun String.toUri(): Uri = Uri.parse(this)

/**
 * Pull the "captured_after" parameter out of an [Href]
 *
 * @throws IllegalStateException throws in absence of captured_after parameter
 */
internal val Href.capturedAfter: String
    get() = "http://example.com/$href"
        .toUri()
        .getQueryParameter("captured_after")
        ?: throw IllegalStateException("No captured_after parameter on next href")

internal fun BackendAsset.toAsset(): Asset {
    requireNotNull(payload) { "No asset metadata found: $this" }

    // TODO: Some assets may not have focal length & aperture ratings. How does the API behave for these?
    return Asset(
        id = AssetId(id),
        captureDate = payload.captureDate,
        cameraBody = payload.xmp.tiff.run {
            "$make $model"
        },
        lens = payload.xmp.aux.lens,
        iso = payload.xmp.exif.iso,
        shutterSpeed = payload.xmp.exif.exposureTime.run {
            "$numerator/$denominator sec"
        },
        aperture = payload.xmp.exif.fNumber.run {
            "ƒ / ${numerator.toFloat() / denominator.toFloat()}"
        },
        focalLength = payload.xmp.exif.focalLength.run {
            "${numerator / denominator} mm"
        },
        rating = payload.rating,
        review = when (payload.picked) {
            true -> Asset.Flag.Picked
            false -> Asset.Flag.Rejected
            null -> null
        },
        keywords = payload.xmp.dc?.subjects ?: emptyList(),
    )
}

internal const val API_PAGE_SIZE = 50

internal class GetAlbumAssetsUseCase @Inject constructor(
    private val albumService: AlbumService,
    private val catalogRepository: CatalogRepository,
) {
    /**
     * Paginate through all assets in an album, and map them to [Asset] models
     *
     * @param albumId the Album to list assets for
     */
    suspend operator fun invoke(albumId: AlbumId): List<Asset> {
        val catalogId = catalogRepository.getCatalog().id

        val albumAssets = buildList {
            // Begin with negative time to (hopefully) fetch all undated assets.
            // We may miss some if there's more than [PAGE_SIZE] undated assets in the album
            var capturedAfter: String? = "-0001-12-31T23:59:59"

            while (capturedAfter != null) {
                val page = albumService.getAlbumAssets(
                    catalogId = catalogId.id,
                    albumId = albumId.id,
                    limit = API_PAGE_SIZE,
                    capturedAfter = capturedAfter,
                )

                addAll(page.resources)

                capturedAfter = page.links?.next?.capturedAfter
            }
        }

        return albumAssets.map { it.asset.toAsset() }
    }
}
