package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AssetsPageResponse(
    val resources: List<AlbumAsset>,
    val links: Links? = null,
)

@Serializable
internal data class AlbumAsset(
    val asset: Asset,
)

@Serializable
internal data class Links(
    val prev: Href? = null,
    val next: Href? = null,
)

@Serializable
internal data class Href(
    val href: String,
)
