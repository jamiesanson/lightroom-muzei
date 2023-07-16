package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class AlbumAssetResponse(
    val resources: List<AlbumAsset>,
)

@Serializable
data class AlbumAsset(
    val asset: Asset,
)
