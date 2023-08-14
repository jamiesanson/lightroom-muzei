package dev.sanson.lightroom

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import dev.sanson.lightroom.data.LoadAlbumWorker
import java.io.InputStream
import java.util.concurrent.TimeUnit

class LightroomAlbumProvider : MuzeiArtProvider() {

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

    // TODO: Check if rendition exists for artwork
    override fun isArtworkValid(artwork: Artwork): Boolean {
        return super.isArtworkValid(artwork)
    }

    // TODO: Take persistent URI from artwork, use headers, generate rendition if needed
    override fun openFile(artwork: Artwork): InputStream {
        return super.openFile(artwork)
    }
}
