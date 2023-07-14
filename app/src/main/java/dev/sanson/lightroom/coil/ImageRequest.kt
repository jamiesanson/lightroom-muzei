package dev.sanson.lightroom.coil

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import okhttp3.Headers.Companion.toHeaders
import javax.inject.Inject

@HiltViewModel
internal class ImageRequestViewModel @Inject constructor(
    private val lightroom: Lightroom,
) : ViewModel() {

    suspend fun buildImageRequest(
        context: Context,
        assetId: AssetId,
        rendition: Rendition,
    ): ImageRequest {
        val imageUrl = with(lightroom) { assetId.asUrl(rendition) }
        val headers = lightroom.getAuthHeaders().toHeaders()

        return ImageRequest.Builder(context)
            .data(imageUrl)
            .headers(headers)
            .build()
    }
}

@Composable
fun rememberImageRequest(
    assetId: AssetId,
    rendition: Rendition = Rendition.SixForty,
    viewModel: ImageRequestViewModel = hiltViewModel(context as ViewModelStoreOwner),
): ImageRequest? {
    if (LocalView.current.isInEditMode) {
        // Avoid ViewModel fetching in previews
        return null
    }

    val context = LocalContext.current

    var request by remember { mutableStateOf<ImageRequest?>(null) }

    LaunchedEffect(assetId, rendition) {
        request = null

        request = viewModel.buildImageRequest(context, assetId, rendition)
    }

    return request
}
