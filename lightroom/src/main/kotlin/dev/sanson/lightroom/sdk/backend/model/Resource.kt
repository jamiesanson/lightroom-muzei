package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Resource<T>(
    val id: String,
    val subtype: String? = null,
    val payload: T? = null,
)
