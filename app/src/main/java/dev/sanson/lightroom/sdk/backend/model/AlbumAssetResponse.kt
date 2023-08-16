package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class AlbumAssetResponse(
    val resources: List<AlbumAsset>,
    val links: Links? = null,
)

@Serializable
data class AlbumAsset(
    val asset: Asset,
)

@Serializable
data class Links(
    val prev: Href? = null,
    val next: Href? = null,
)

@Serializable
data class Href(
    val href: String,
)
