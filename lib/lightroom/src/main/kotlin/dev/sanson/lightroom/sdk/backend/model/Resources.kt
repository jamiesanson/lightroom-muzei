package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Resources<T>(
    val resources: List<Resource<T>>,
)
