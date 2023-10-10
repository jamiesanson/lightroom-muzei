package dev.sanson.lightroom.muzei

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.apps.muzei.api.provider.ProviderClient
import com.google.android.apps.muzei.api.provider.ProviderContract.getProviderClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.data.config.Config
import dev.sanson.lightroom.sdk.Lightroom
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker, which coordinates loading the selected album and populating
 * the Muzei [ProviderClient].
 */
@HiltWorker
class LoadAlbumWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val lightroom: Lightroom,
    private val configStore: DataStore<Config?>,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val config = configStore.data.first() ?: return Result.failure()
        val albumProvider = getProviderClient<LightroomAlbumProvider>(context = applicationContext)

        val previouslyAddedAssets = albumProvider.getArtwork(
            contentResolver = applicationContext.contentResolver,
        ).mapNotNull { it.token }

        val artworks = lightroom.loadArtwork(config)
            .filterNot { albumAsset ->
                albumAsset.token in previouslyAddedAssets
            }

        albumProvider.addArtwork(artworks)

        return Result.success()
    }
}
