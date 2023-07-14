package dev.sanson.lightroom.sdk.model

data class Album(
    val id: AlbumId,
    val name: String,
    val folder: String?,
    val cover: AssetId?,
    val assets: List<AssetId>,
)
