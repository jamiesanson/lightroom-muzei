package dev.sanson.lightroom.unsplash

import android.os.Parcelable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.ui.component.HyperlinkText
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import dev.sanson.lightroom.unsplash.api.Photo
import dev.sanson.lightroom.unsplash.api.UnsplashService
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

private const val UNSPLASH_REFERRAL = "utm_source=Lightroom%20for%20Muzei&utm_medium=referral"
private fun unsplash(path: String) = "https://unsplash.com$path$UNSPLASH_REFERRAL"

@HiltViewModel
class RandomImageViewModel @Inject constructor(
    private val unsplashService: UnsplashService,
) : ViewModel() {

    private var cachedPhoto: Photo? = null

    suspend fun getRandomPhoto(): Photo? {
        if (cachedPhoto == null) {
            cachedPhoto = runCatching { unsplashService.getRandomPhoto() }
                .getOrNull()
        }

        return cachedPhoto
    }
}

@Composable
fun rememberRandomImage(
    unsplashService: RandomImageViewModel = hiltViewModel(LocalContext.current as ViewModelStoreOwner),
): RandomImage? {
    if (LocalView.current.isInEditMode) {
        // Avoid ViewModel fetching in previews
        return null
    }

    var image by rememberSaveable { mutableStateOf<RandomImage?>(null) }

    LaunchedEffect(true) {
        if (image == null) {
            val photo = unsplashService.getRandomPhoto()

            if (photo != null) {
                image = RandomImage(
                    url = photo.urls.regular,
                    attribution = Attribution(
                        name = photo.user.name,
                        username = photo.user.username,
                    ),
                )
            }
        }
    }

    return image
}

@Parcelize
data class RandomImage(
    val url: String,
    val attribution: Attribution,
) : Parcelable

@Parcelize
data class Attribution(
    val name: String,
    val username: String,
) : Parcelable

@Composable
fun AttributionChip(
    name: String,
    username: String,
    modifier: Modifier = Modifier,
) {
    // Always use dark theme for attribution chip
    MuzeiLightroomTheme(darkTheme = true) {
        Box(
            modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.36f),
                    shape = RoundedCornerShape(50),
                ),
        ) {
            HyperlinkText(
                fullText = "Photo by $name on Unsplash",
                hyperLinks = persistentMapOf(
                    name to unsplash("/@$username"),
                    "Unsplash" to unsplash("/"),
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
}

@Preview
@Composable
fun AttributionTextPreview() {
    MuzeiLightroomTheme {
        AttributionChip(modifier = Modifier, name = "Jamie Sanson", username = "jamiesanson")
    }
}
