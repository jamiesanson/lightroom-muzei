package dev.sanson.lightroom.sdk.model

import kotlinx.datetime.Instant

data class Asset(
    val id: AssetId,
    val captureDate: Instant,
    val cameraBody: String,
    val lens: String,
    val iso: Int,
    val shutterSpeed: String,
    val aperture: String,
    val focalLength: String,
)
