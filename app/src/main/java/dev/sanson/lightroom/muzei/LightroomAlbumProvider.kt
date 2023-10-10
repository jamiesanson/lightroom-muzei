package dev.sanson.lightroom.muzei

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import coil.Coil
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.coil.awaitSuccessfulImageRequest
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

class LightroomAlbumProvider : MuzeiArtProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface EntryPointAccessor {
        val lightroom: Lightroom
    }

    override fun onLoadRequested(initial: Boolean) {
        val workManager = WorkManager.getInstance(requireNotNull(context))

        val request = OneTimeWorkRequestBuilder<LoadAlbumWorker>()
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED,
                    requiresStorageNotLow = true,
                ),
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                backoffDelay = 10L,
                timeUnit = TimeUnit.MINUTES,
            )
            .setExpedited(
                policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST,
            )
            .build()

        workManager
            .enqueueUniqueWork("load_album", ExistingWorkPolicy.REPLACE, request)
    }

    override fun openFile(artwork: Artwork): InputStream {
        val context = requireNotNull(context)

        val lightroom = EntryPointAccessors
            .fromApplication<EntryPointAccessor>(context)
            .lightroom

        return runBlocking(Dispatchers.IO) {
            val assetId =
                AssetId(requireNotNull(artwork.token) { "No token found for artwork $artwork" })

            // Generate rendition for image
            lightroom.generateRendition(
                asset = assetId,
                rendition = Rendition.Full,
            )

            // Await rendition download
            val request = lightroom.awaitSuccessfulImageRequest(
                context = context,
                assetId = assetId,
                rendition = Rendition.Full,
            )

            val result = Coil.imageLoader(context).execute(request)

            // Map downloaded bitmap into an inputstream
            val outputStream = ByteArrayOutputStream()

            result.drawable
                ?.toBitmap()
                ?.compress(Bitmap.CompressFormat.PNG, 0, outputStream)

            ByteArrayInputStream(outputStream.toByteArray())
        }
    }
}
