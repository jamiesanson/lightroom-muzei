package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class Resource<T>(
    val id: String,
    val subtype: String? = null,
    val payload: T? = null,
)
