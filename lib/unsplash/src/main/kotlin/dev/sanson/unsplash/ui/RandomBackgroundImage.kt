package dev.sanson.unsplash.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.sanson.unsplash.rememberRandomImage

@Composable
fun RandomBackgroundImage(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    val backgroundImage = rememberRandomImage()
    if (backgroundImage != null) {
        Box(modifier) {
            var showAttribution by rememberSaveable { mutableStateOf(false) }
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(backgroundImage.url)
                        .crossfade(300)
                        .build(),
                contentDescription = "",
                placeholder = ColorPainter(backgroundColor),
                onSuccess = {
                    showAttribution = true
                },
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxSize(),
            )

            AnimatedVisibility(
                visible = showAttribution,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .systemBarsPadding(),
            ) {
                AttributionChip(
                    name = backgroundImage.attribution.name,
                    username = backgroundImage.attribution.username,
                    modifier = Modifier.padding(16.dp),
                )
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.12f)),
            )
        }
    }
}
