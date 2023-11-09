package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Album(
    val name: String,
    val parent: Asset? = null,
    val cover: Asset? = null,
)
