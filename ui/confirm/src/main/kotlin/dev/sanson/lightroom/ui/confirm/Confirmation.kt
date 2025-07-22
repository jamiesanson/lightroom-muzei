// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTime::class)

package dev.sanson.lightroom.ui.confirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageResult
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.ui.LightroomForMuzeiTheme
import dev.sanson.lightroom.common.ui.component.PreviewLightDark
import dev.sanson.lightroom.common.ui.component.StepHeader
import dev.sanson.lightroom.screens.ConfirmationScreen
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.CatalogId
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.delay
import nz.sanson.lightroom.coil.rememberImageResult
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@CircuitInject(ConfirmationScreen::class, SingletonComponent::class)
@Composable
fun Confirmation(
    state: ConfirmState,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize()) {
        Box(Modifier.systemBarsPadding()) {
            Column {
                StepHeader(
                    stepNumber = state.stepNumber,
                    stepName = stringResource(R.string.confirm),
                    modifier =
                        Modifier
                            .padding(24.dp)
                            .padding(top = 64.dp),
                )

                Crossfade(state, label = "Confirmation screen content crossfade") { currentState ->
                    if (currentState !is ConfirmState.Loaded) {
                        LoadingProgress(
                            state = currentState,
                            modifier = Modifier.padding(16.dp),
                        )
                    } else {
                        FirstImageLoaded(
                            wallpaper = currentState.firstWallpaper,
                            wallpaperAge = currentState.firstWallpaperAge,
                            wallpaperCount = currentState.artwork.size,
                            onConfirm = { currentState.eventSink(ConfirmEvent.OnFinish) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingProgress(
    state: ConfirmState,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        LoadingRow(
            isLoading = state !is ConfirmState.LoadingFirstImage,
            text = stringResource(R.string.loading_images),
            subtitle = stringResource(R.string.loading_images_subtitle),
        )

        Spacer(modifier = Modifier.size(24.dp))

        LoadingRow(
            isLoading = state !is ConfirmState.Loaded,
            text = stringResource(R.string.fetching_wallpaper),
        )
    }
}

@Composable
private fun LoadingRow(
    isLoading: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp)) {
            Crossfade(
                isLoading,
                label = "Loading icon crossfade",
                modifier = Modifier.align(Alignment.Center),
            ) { loading ->
                if (loading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Icon(
                        Icons.Default.Check,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        val textColor by animateColorAsState(
            targetValue =
                if (isLoading) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(0.54f)
                },
            label = "Loading row text color",
        )

        var showSubtitle by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(subtitle) {
            if (subtitle != null && !showSubtitle) {
                delay(3.seconds)
                showSubtitle = true
            }
        }

        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
            )

            AnimatedVisibility(visible = showSubtitle) {
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FirstImageLoaded(
    wallpaper: Asset,
    wallpaperAge: String,
    wallpaperCount: Int,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        Spacer(Modifier.size(16.dp))

        WallpaperPreview(
            asset = wallpaper,
            modifier = Modifier.fillMaxHeight(0.7f),
        )

        Spacer(Modifier.size(16.dp))

        Text(
            stringResource(R.string.images_loaded, wallpaperCount),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Text(
            stringResource(R.string.start_time_ago, wallpaperAge),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onConfirm,
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 24.dp),
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp),
        ) {
            Spacer(Modifier.size(8.dp))

            Text(
                text = "Confirm",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Spacer(Modifier.size(16.dp))

            Icon(Icons.Default.Check, contentDescription = "")
        }
    }
}

@Composable
private fun WallpaperPreview(
    asset: Asset,
    modifier: Modifier = Modifier,
) {
    val result =
        rememberImageResult(
            assetId = asset.id,
            catalogId = asset.catalogId,
            rendition = Rendition.Full,
        )

    Crossfade(targetState = result, modifier = modifier, label = "First wallpaper") { imageResult ->
        if (imageResult != null) {
            WallpaperCutout(imageResult = imageResult)
        } else {
            WallpaperLoading()
        }
    }
}

@Composable
private fun WallpaperLoading(modifier: Modifier = Modifier) {
    val windowInfo = LocalWindowInfo.current

    val screenAspectRatio =
        remember(windowInfo) {
            windowInfo.containerSize.width.toFloat() / windowInfo.containerSize.height.toFloat()
        }

    val backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f)

    Box(
        modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        Box(
            Modifier
                .aspectRatio(screenAspectRatio, matchHeightConstraintsFirst = true)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(2.dp, backgroundColor, RoundedCornerShape(12.dp))
                .align(Alignment.Center),
        )
    }
}

@Composable
private fun WallpaperCutout(
    imageResult: ImageResult,
    modifier: Modifier = Modifier,
) {
    val windowInfo = LocalWindowInfo.current

    val screenAspectRatio =
        remember(windowInfo) {
            windowInfo.containerSize.width.toFloat() / windowInfo.containerSize.height.toFloat()
        }

    Box(
        modifier
            .fillMaxSize(),
    ) {
        AsyncImage(
            model = imageResult.request,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxSize()
                    .alpha(0.1f),
        )

        AsyncImage(
            model = imageResult.request,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .aspectRatio(screenAspectRatio, matchHeightConstraintsFirst = true)
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.Center),
            contentDescription = "",
        )
    }
}

@PreviewLightDark
@Composable
private fun ChooseSourcePreview() {
    val dummyAsset =
        Asset(
            AssetId(""),
            CatalogId(""),
            Instant.DISTANT_PAST,
            "",
            "",
            1,
            "",
            "",
            "",
            emptyList(),
        )

    val loadedState =
        ConfirmState.Loaded(
            stepNumber = 4,
            firstWallpaper = dummyAsset,
            firstWallpaperAge = "3 months",
            artwork = emptyList(),
            eventSink = {},
        )

    LightroomForMuzeiTheme {
        Confirmation(
            state = loadedState,
        )
    }
}
