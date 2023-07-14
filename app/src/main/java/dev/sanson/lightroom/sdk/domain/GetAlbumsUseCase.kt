package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.AlbumService
import dev.sanson.lightroom.sdk.backend.CatalogService
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AssetId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val albumService: AlbumService,
    private val catalogService: CatalogService,
) {

    suspend operator fun invoke(): List<Album> = withContext(Dispatchers.IO) {
        val catalog = catalogService.getCatalog()
        val albums = albumService.getAlbums(catalogId = catalog.id)

        return@withContext albums.resources.map {
            Album(
                id = AlbumId(id = it.id),
                name = it.payload.name,
                cover = it.payload.cover?.id?.let(::AssetId),
            )
        }
    }
}
