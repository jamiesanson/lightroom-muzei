package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("first_name")
    val firstName: String,
)
