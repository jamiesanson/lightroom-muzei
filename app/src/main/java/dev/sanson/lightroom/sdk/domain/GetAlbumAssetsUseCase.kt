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
            .getAlbumAssets(catalogId = catalogId.id, albumId = albumId.id)
            .resources
            .map { AssetId(it.asset.id) }
            .map { TODO("Finish mapping operation by deserialising the right information") }
    }
}
