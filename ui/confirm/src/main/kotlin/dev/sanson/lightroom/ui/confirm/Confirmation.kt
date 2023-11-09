package dev.sanson.lightroom.ui.confirm

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.ui.component.DarkModePreviews
import dev.sanson.lightroom.common.ui.component.LightroomCard
import dev.sanson.lightroom.core.ui.MuzeiLightroomTheme
import dev.sanson.lightroom.screens.ConfirmationScreen
import dev.sanson.lightroom.sdk.model.AssetId
import nz.sanson.lightroom.coil.rememberImageRequest

@CircuitInject(ConfirmationScreen::class, SingletonComponent::class)
@Composable
fun Confirmation(
    state: ConfirmState,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.DarkGray

    Box(
        modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        if (state is ConfirmState.Loaded) {
            FirstImageBackground(assetId = state.firstWallpaperId)
        }

        ConfirmationDialog(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
        )
    }
}

@Composable
private fun ConfirmationDialog(
    state: ConfirmState,
    modifier: Modifier = Modifier,
) {
    LightroomCard(modifier) {
        AnimatedContent(
            targetState = state,
            label = "Loading state",
        ) { currentState ->
            when (currentState) {
                is ConfirmState.Loaded ->
                    ImagesLoaded(
                        imageCount = remember(currentState) { currentState.artwork.size },
                        firstAssetDate = currentState.firstArtworkCaptureDate,
                        onComplete = { currentState.eventSink(ConfirmEvent.OnFinish) },
                    )

                is ConfirmState.LoadingArtwork ->
                    LoadingArtwork()

                is ConfirmState.LoadingFirstImage ->
                    LoadingFirstImage(
                        imageCount = remember(currentState) { currentState.artwork.size },
                    )
            }
        }
    }
}

@Composable
private fun LoadingArtwork(
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = "Fetching images from Lightroom",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoadingFirstImage(
    imageCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = "Loaded $imageCount images into Muzei, exporting your first wallpaper.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ImagesLoaded(
    imageCount: Int,
    firstAssetDate: String,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = "You're all set!",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = "We've loaded $imageCount images into Muzei. Let's start with this image, taken on $firstAssetDate.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.size(24.dp))

        OutlinedButton(
            onClick = onComplete,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Complete setup",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(Modifier.size(2.dp))
    }
}

@Composable
private fun FirstImageBackground(
    assetId: AssetId,
    modifier: Modifier = Modifier,
) {
    val lightroomImageRequest = rememberImageRequest(assetId = assetId)
    val context = LocalContext.current

    AsyncImage(
        model = remember(lightroomImageRequest) {
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

@DarkModePreviews
@Composable
fun ChooseSourcePreview() {
    MuzeiLightroomTheme {
        Confirmation(
            state = ConfirmState.Loaded(
                firstWallpaperId = AssetId("1"),
                firstArtworkCaptureDate = "5 Dec 2023",
                artwork = emptyList(),
                eventSink = {},
            ),
        )
    }
}
