package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class Resources<T>(
    val resources: List<Resource<T>>,
)
