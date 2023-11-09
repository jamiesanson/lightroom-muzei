package nz.sanson.lightroom.coil

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.getImageAuthHeaders
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.delay
import okhttp3.Headers.Companion.toHeaders

val LocalLightroom = staticCompositionLocalOf<Lightroom> { error("Lightroom not supplied") }

internal suspend fun Lightroom.buildImageRequest(
    context: Context,
    assetId: AssetId,
    rendition: Rendition,
): ImageRequest {
    val imageUrl = assetId.asUrl(rendition)
    val headers = getImageAuthHeaders().toHeaders()

    return ImageRequest.Builder(context)
        .data(imageUrl)
        .headers(headers)
        .build()
}

suspend fun Lightroom.awaitSuccessfulImageRequest(
    context: Context,
    assetId: AssetId,
    rendition: Rendition,
): ImageRequest {
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

        val pendingRequest = buildImageRequest(context, assetId, rendition)

        result = context.imageLoader.execute(pendingRequest)
    }

    Log.v("ImageRequest", "Successful result: $result")

    return result.request
}

@Composable
fun rememberImageRequest(
    lightroom: Lightroom = LocalLightroom.current,
    assetId: AssetId,
    rendition: Rendition = Rendition.SixForty,
): ImageRequest? {
    if (LocalView.current.isInEditMode) {
        return null
    }

    return rememberImageRequest(
        assetId = assetId,
        rendition = rendition,
        context = LocalContext.current,
        lightroom = lightroom,
    )
}

@Composable
private fun rememberImageRequest(
    assetId: AssetId,
    context: Context,
    lightroom: Lightroom,
    rendition: Rendition = Rendition.SixForty,
): ImageRequest? {
    var request by remember { mutableStateOf<ImageRequest?>(null) }

    LaunchedEffect(assetId, rendition) {
        request = null
        request = lightroom.awaitSuccessfulImageRequest(context, assetId, rendition)
    }

    return request
}
