package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.AlbumService
import dev.sanson.lightroom.sdk.backend.AssetsService
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.CatalogId
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val albumService: AlbumService,
    private val assetsService: AssetsService,
    private val catalogRepository: CatalogRepository,
) {

    suspend operator fun invoke(): List<Album> = withContext(Dispatchers.IO) {
        val catalog = catalogRepository.getCatalog()
        val albums = albumService.getAlbums(catalogId = catalog.id.id)

        val folders = albums.resources
            .filter { it.subtype == "collection_set" }

        val domainAlbums = albums.resources
            // Clear out these oddballs
            .filter { it.payload != null }
            // Get rid of folder/collection set albums
            .filterNot { folders.any { (id, _) -> it.id == id } }
            .map { album ->
                requireNotNull(album.payload)

                Album(
                    id = AlbumId(id = album.id),
                    folder = folders.firstOrNull { (id, _) -> album.payload.parent?.id == id }?.payload?.name,
                    name = album.payload.name,
                    cover = album.payload.cover?.id?.let(::AssetId),
                    assets = emptyList(),
                )
            }.map { album ->
                async {
                    val assets = loadAlbumAssets(
                        catalogId = catalog.id,
                        albumId = album.id,
                    )

                    album.copy(
                        assets = assets,
                        cover = album.cover ?: assets.firstOrNull()?.also {
                            tryGenerateRendition(catalogId = catalog.id, assetId = it)
                        },
                    )
                }
            }

        return@withContext domainAlbums.awaitAll()
    }

    private suspend fun loadAlbumAssets(catalogId: CatalogId, albumId: AlbumId): List<AssetId> {
        return albumService
            .getAlbumAssets(catalogId = catalogId.id, albumId = albumId.id)
            .resources
            .map { AssetId(it.asset.id) }
    }

    private suspend fun tryGenerateRendition(catalogId: CatalogId, assetId: AssetId) = runCatching {
        assetsService.generateRendition(
            catalogId = catalogId.id,
            assetId = assetId.id,
            renditions = Rendition.Full.code,
        )
    }
}
