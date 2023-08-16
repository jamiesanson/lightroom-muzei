package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.AlbumService
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.AssetId
import javax.inject.Inject

class GetAlbumAssetsUseCase @Inject constructor(
    private val albumService: AlbumService,
    private val catalogRepository: CatalogRepository,
) {

    suspend operator fun invoke(albumId: AlbumId): List<Asset> {
        val catalogId = catalogRepository.getCatalog().id

        return albumService
            .getAlbumAssets(catalogId = catalogId.id, albumId = albumId.id, limit = 10)
            .resources
            .map { it.asset }
            .map { asset ->
                requireNotNull(asset.payload) { "No asset metadata found: $asset" }

                Asset(
                    id = AssetId(asset.id),
                    captureDate = asset.payload.captureDate,
                    cameraBody = asset.payload.xmp.tiff.run {
                        "$make $model"
                    },
                    lens = asset.payload.xmp.aux.lens,
                    iso = asset.payload.xmp.exif.iso,
                    shutterSpeed = asset.payload.xmp.exif.exposureTime.run {
                        "$numerator/$denominator sec"
                    },
                    aperture = asset.payload.xmp.exif.fNumber.run {
                        "ƒ / ${numerator / denominator}"
                    },
                    focalLength = asset.payload.xmp.exif.focalLength.run {
                        "${numerator / denominator} mm"
                    },
                )
            }
    }
}
