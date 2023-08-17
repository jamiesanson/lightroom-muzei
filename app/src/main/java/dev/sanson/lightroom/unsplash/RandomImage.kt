package dev.sanson.lightroom.unsplash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.ui.component.HyperlinkText
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import dev.sanson.lightroom.unsplash.api.UnsplashService
import kotlinx.collections.immutable.persistentMapOf
import javax.inject.Inject

private const val UNSPLASH_REFERRAL = "utm_source=Lightroom%20for%20Muzei&utm_medium=referral"

@HiltViewModel
private class RandomImageViewModel @Inject constructor(
    val unsplashService: UnsplashService,
) : ViewModel()

@Composable
fun rememberRandomImage(): RandomImage? {
    val unsplashService = hiltViewModel<RandomImageViewModel>().unsplashService

    var image by rememberSaveable { mutableStateOf<RandomImage?>(null) }

    LaunchedEffect(true) {
        val photo = runCatching { unsplashService.getRandomPhoto() }
            .getOrNull()

        if (photo != null) {
            image = RandomImage(
                url = photo.urls.regular,
                attribution = { modifier ->
                    AttributionText(
                        modifier = modifier,
                        name = photo.user.name,
                        username = photo.user.username,
                    )
                },
            )
        }
    }

    return image
}

data class RandomImage(
    val url: String,
    val attribution: @Composable (Modifier) -> Unit,
)

@Composable
private fun AttributionText(
    name: String,
    username: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(50),
            ),
    ) {
        HyperlinkText(
            fullText = "Photo by $name on Unsplash",
            hyperLinks = persistentMapOf(
                name to "https://unsplash.com/@$username?$UNSPLASH_REFERRAL",
                "Unsplash" to "https://unsplash.com/?$UNSPLASH_REFERRAL",
            ),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            linkTextColor = MaterialTheme.colorScheme.secondary,
            linkTextDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Preview
@Composable
fun AttributionTextPreview() {
    MuzeiLightroomTheme {
        AttributionText(modifier = Modifier, name = "Jamie Sanson", username = "jamiesanson")
    }
}
