package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val name: String,
    val parent: Asset? = null,
    val cover: Asset? = null,
) {
    val isFolder = parent?.id == null
}
