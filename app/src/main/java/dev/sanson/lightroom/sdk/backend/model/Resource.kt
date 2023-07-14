package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class Resource<T>(
    val id: String,
    val payload: T,
)
