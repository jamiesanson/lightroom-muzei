package dev.sanson.lightroom.ui.confirm

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.ui.MuzeiLightroomTheme
import dev.sanson.lightroom.common.ui.component.PreviewLightDark
import dev.sanson.lightroom.common.ui.component.StepHeader
import dev.sanson.lightroom.screens.ConfirmationScreen
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.CatalogId
import kotlinx.datetime.Instant
import nz.sanson.lightroom.coil.rememberImageRequest

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
                        FirstImageBackground(asset = currentState.firstWallpaper)
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
            text = "Loading images",
        )

        Spacer(modifier = Modifier.size(24.dp))

        LoadingRow(
            isLoading = state !is ConfirmState.Loaded,
            text = "Fetching your first wallpaper",
        )
    }
}

@Composable
private fun LoadingRow(
    isLoading: Boolean,
    text: String,
    modifier: Modifier = Modifier,
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

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
        )
    }
}

@Composable
private fun FirstImageBackground(
    asset: Asset,
    modifier: Modifier = Modifier,
) {
    val lightroomImageRequest = rememberImageRequest(asset = asset)
    val context = LocalContext.current

    AsyncImage(
        model =
            remember(lightroomImageRequest) {
                val request = lightroomImageRequest ?: return@remember null

                request
                    .newBuilder(context)
                    .crossfade(300)
                    .build()
            },
        placeholder = ColorPainter(Color.Transparent),
        contentScale = ContentScale.Crop,
        contentDescription = "First wallpaper image",
        modifier = modifier.fillMaxSize(),
    )
}

@PreviewLightDark
@Composable
private fun ChooseSourcePreview() {
    val dummyAsset =
        Asset(
            AssetId(""), CatalogId(""), Instant.DISTANT_PAST, "",
            "", 1, "", "", "", emptyList(),
        )

    val loadedState =
        ConfirmState.Loaded(
            stepNumber = 4,
            firstWallpaper = dummyAsset,
            firstArtworkCaptureDate = Instant.DISTANT_PAST,
            artwork = emptyList(),
            eventSink = {},
        )

    MuzeiLightroomTheme {
        Confirmation(
            state = loadedState,
        )
    }
}
