package dev.sanson.lightroom.sdk.model

data class Album(
    override val id: AlbumId,
    override val catalogId: CatalogId,
    override val name: String,
    val cover: AssetId?,
    val assets: List<AssetId>,
) : AlbumTreeItem
