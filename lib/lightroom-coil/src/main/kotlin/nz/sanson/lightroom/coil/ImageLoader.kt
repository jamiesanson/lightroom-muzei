package nz.sanson.lightroom.coil

import android.content.Context
import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
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

val LocalLightroomImageLoader =
    staticCompositionLocalOf<ImageLoader> { error("Lightroom ImageLoader not supplied") }

/**
 * Create a new instance of an [ImageLoader]
 *
 * @param context Context to use with Image requests
 * @return new [ImageLoader]
 */
fun Lightroom.createImageLoader(context: Context): ImageLoader {
    return DefaultImageLoader(context = context, lightroom = this)
}

interface ImageLoader {
    /**
     * Create a new Coil [ImageRequest]
     *
     * @param assetId the asset to use in the image request
     * @param rendition the scale of the rendition to be requested
     */
    suspend fun newRequest(assetId: AssetId, rendition: Rendition): ImageRequest

    /**
     * Await an image request
     *
     * This function will poll the Lightroom API, and return the latest successful [ImageRequest]
     */
    suspend fun ImageRequest.await(): ImageRequest
}

internal class DefaultImageLoader(
    private val context: Context,
    private val lightroom: Lightroom,
) : ImageLoader {
    override suspend fun ImageRequest.await(): ImageRequest {
        var result: ImageResult? = null
        var request: ImageRequest = this

        // Retry image loading until it succeeds. This should really only happen when there's a pending
        // rendition, so might be able to do more to figure that out.
        while (result !is SuccessResult) {
            if (result != null) {
                delay(2000)
                Log.v(
                    "ImageRequest",
                    "Request failed $request",
                    (result as ErrorResult).throwable,
                )
            }

            request = newBuilder(context).build()
            result = context.imageLoader.execute(request)
        }

        Log.v("ImageRequest", "Successful result: $result")

        return result.request
    }

    override suspend fun newRequest(assetId: AssetId, rendition: Rendition): ImageRequest {
        val imageUrl = with(lightroom) { assetId.asUrl(rendition) }
        val headers = lightroom.getImageAuthHeaders().toHeaders()

        return ImageRequest.Builder(context)
            .data(imageUrl)
            .headers(headers)
            .build()
    }
}