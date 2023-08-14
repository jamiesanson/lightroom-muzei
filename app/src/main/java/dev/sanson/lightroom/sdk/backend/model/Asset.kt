package dev.sanson.lightroom.sdk.backend.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    val id: String,
    val payload: Payload,
)

@Serializable
data class Payload(
    val captureDate: Instant,
)
