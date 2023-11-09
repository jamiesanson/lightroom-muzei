package nz.sanson.lightroom.coil

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import coil.request.ImageRequest
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition

@Composable
fun rememberImageRequest(
    assetId: AssetId,
    rendition: Rendition = Rendition.SixForty,
): ImageRequest? {
    if (LocalView.current.isInEditMode) {
        return null
    }

    val imageLoader = LocalLightroomImageLoader.current

    var request by remember { mutableStateOf<ImageRequest?>(null) }

    LaunchedEffect(assetId, rendition) {
        request = null
        request = with(imageLoader) {
            newRequest(assetId, rendition).await()
        }
    }

    return request
}
