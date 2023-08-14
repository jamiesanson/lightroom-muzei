package dev.sanson.lightroom.sdk.model

import kotlinx.datetime.LocalDateTime

data class Asset(
    val id: AssetId,
    val captureDate: LocalDateTime,
    val cameraBody: String,
    val lens: String,
    val iso: Int,
    val shutterSpeed: String,
    val aperture: String,
    val focalLength: String,
)
