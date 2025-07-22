// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.AlbumService
import dev.sanson.lightroom.sdk.backend.model.Resource
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AlbumTreeItem
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.CatalogId
import dev.sanson.lightroom.sdk.model.CollectionSet
import dev.sanson.lightroom.sdk.model.CollectionSetId
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import dev.sanson.lightroom.sdk.backend.model.Album as BackendAlbum

internal class GetAlbumsUseCase
    @Inject
    constructor(
        private val albumService: AlbumService,
        private val catalogRepository: CatalogRepository,
        private val generateRendition: GenerateRenditionUseCase,
    ) {
        suspend operator fun invoke(): List<AlbumTreeItem> =
            withContext(Dispatchers.IO) {
                val catalog = catalogRepository.getCatalog()

                return@withContext albumService
                    .getAlbums(catalogId = catalog.id.id)
                    .resources
                    // Clear out these oddballs
                    .filter { it.payload != null }
                    // For resources into tree-based representation
                    .findChildren(catalogId = catalog.id, parentId = null)
                    // Load assets for albums
                    .withAssets(catalogId = catalog.id)
                    // Await all album loads
                    .awaitAll()
            }

        private suspend fun loadAlbumAssets(
            catalogId: CatalogId,
            albumId: AlbumId,
        ): List<AssetId> =
            albumService
                .getAlbumAssets(catalogId = catalogId.id, albumId = albumId.id)
                .resources
                .map { AssetId(it.asset.id) }

        /**
         * Recurse to populate a tree of [AlbumTreeItem]s
         */
        private fun List<Resource<BackendAlbum>>.findChildren(
            catalogId: CatalogId,
            parentId: String?,
        ): List<AlbumTreeItem> =
            buildList {
                val children = this@findChildren.filter { it.payload?.parent?.id == parentId }

                addAll(
                    children.map { resource ->
                        val payload = requireNotNull(resource.payload)
                        when (resource.subtype) {
                            "collection" ->
                                Album(
                                    id = AlbumId(id = resource.id),
                                    catalogId = catalogId,
                                    name = payload.name,
                                    cover = payload.cover?.id?.let(::AssetId),
                                    assets = emptyList(),
                                )

                            "collection_set" ->
                                CollectionSet(
                                    id = CollectionSetId(resource.id),
                                    catalogId = catalogId,
                                    name = payload.name,
                                    children =
                                        (this@findChildren - children.toSet())
                                            .findChildren(catalogId = catalogId, parentId = resource.id),
                                )

                            else ->
                                error("Unsupported subtype: ${resource.subtype}")
                        }
                    },
                )
            }

        /**
         * Depth-first traversal to grab assets for all albums in the tree
         */
        private suspend fun List<AlbumTreeItem>.withAssets(catalogId: CatalogId): List<Deferred<AlbumTreeItem>> =
            coroutineScope {
                return@coroutineScope map {
                    when (it) {
                        is Album ->
                            async {
                                val assets =
                                    loadAlbumAssets(
                                        catalogId = catalogId,
                                        albumId = it.id,
                                    )

                                it.copy(
                                    assets = assets,
                                    cover =
                                        it.cover ?: assets.firstOrNull()?.also { assetId ->
                                            generateRendition(assetId = assetId, rendition = Rendition.Full)
                                        },
                                )
                            }

                        is CollectionSet ->
                            async {
                                it.copy(
                                    children = it.children.withAssets(catalogId).awaitAll(),
                                )
                            }
                    }
                }
            }
    }
