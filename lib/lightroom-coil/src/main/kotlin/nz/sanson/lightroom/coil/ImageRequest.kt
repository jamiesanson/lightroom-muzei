// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package nz.sanson.lightroom.coil

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import coil.request.ImageRequest
import coil.request.ImageResult
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.CatalogId
import dev.sanson.lightroom.sdk.model.Rendition

@Composable
fun rememberImageRequest(
    assetId: AssetId,
    catalogId: CatalogId,
    rendition: Rendition = Rendition.SixForty,
): ImageRequest? =
    rememberImageResult(
        assetId = assetId,
        catalogId = catalogId,
        rendition = rendition,
    )?.request

@Composable
fun rememberImageResult(
    assetId: AssetId,
    catalogId: CatalogId,
    rendition: Rendition = Rendition.SixForty,
): ImageResult? {
    if (LocalView.current.isInEditMode) {
        return null
    }

    val imageLoader = LocalLightroomImageLoader.current

    var result by remember { mutableStateOf<ImageResult?>(null) }

    LaunchedEffect(assetId, catalogId, rendition) {
        result = null
        result =
            with(imageLoader) {
                newRequest(assetId, catalogId, rendition).await()
            }
    }

    return result
}
