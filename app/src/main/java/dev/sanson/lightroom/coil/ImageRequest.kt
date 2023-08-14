package dev.sanson.lightroom.coil

import android.content.Context
import android.util.Log
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
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.getAuthHeaders
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.delay
import okhttp3.Headers.Companion.toHeaders
import javax.inject.Inject

@HiltViewModel
internal class ImageRequestViewModel @Inject constructor(
    val lightroom: Lightroom,
) : ViewModel()

suspend fun Lightroom.buildImageRequest(
    context: Context,
    assetId: AssetId,
    rendition: Rendition,
): ImageRequest {
    val imageUrl = assetId.asUrl(rendition)
    val headers = getAuthHeaders().toHeaders()

    return ImageRequest.Builder(context)
        .data(imageUrl)
        .headers(headers)
        .listener()
        .build()
}

@Composable
fun rememberImageRequest(
    assetId: AssetId,
    rendition: Rendition = Rendition.SixForty,
): ImageRequest? {
    if (LocalView.current.isInEditMode) {
        // Avoid ViewModel fetching in previews
        return null
    }

    return rememberImageRequest(
        assetId = assetId,
        rendition = rendition,
        context = LocalContext.current,
    )
}

@Composable
private fun rememberImageRequest(
    assetId: AssetId,
    context: Context,
    rendition: Rendition = Rendition.SixForty,
    viewModel: ImageRequestViewModel = hiltViewModel(context as ViewModelStoreOwner),
): ImageRequest? {
    var request by remember { mutableStateOf<ImageRequest?>(null) }

    LaunchedEffect(assetId, rendition) {
        request = null

        var result: ImageResult? = null

        // Retry image loading until it succeeds. This should really only happen when there's a pending
        // rendition, so might be able to do more to figure that out.
        while (result !is SuccessResult) {
            if (result != null) {
                delay(2000)
                Log.v(
                    "ImageRequest",
                    "Retrying request for asset ${assetId.id}",
                    (result as ErrorResult).throwable,
                )
            }

            val pendingRequest = viewModel.lightroom.buildImageRequest(context, assetId, rendition)

            result = context.imageLoader.execute(pendingRequest)
        }

        request = result.request
    }

    return request
}
