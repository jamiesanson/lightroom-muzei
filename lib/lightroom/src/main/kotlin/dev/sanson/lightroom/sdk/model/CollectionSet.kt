package dev.sanson.lightroom.sdk.model

data class CollectionSet(
    override val id: CollectionSetId,
    override val catalogId: CatalogId,
    override val name: String,
    val children: List<AlbumTreeItem>,
) : AlbumTreeItem
