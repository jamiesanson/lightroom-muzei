package dev.sanson.lightroom.data

import android.content.Context
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderClient
import com.google.android.apps.muzei.api.provider.ProviderContract.getProviderClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.LightroomAlbumProvider
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime

/**
 * WorkManager worker, which coordinates loading the selected album and populating
 * the Muzei [ProviderClient].
 */
@HiltWorker
class LoadAlbumWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val lightroom: Lightroom,
    private val albumIdStore: DataStore<AlbumId?>,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val albumId = albumIdStore.data.first() ?: return Result.failure()
        val albumProvider = getProviderClient<LightroomAlbumProvider>(context = applicationContext)

        val album = lightroom.getAlbums().first { it.id == albumId }

        val previouslyAddedAssets: List<Artwork> = applicationContext.contentResolver
            .query(albumProvider.contentUri, null, null, null, null)
            ?.use { cursor ->
                cursor.moveToFirst()

                buildList {
                    while (!cursor.isAfterLast) {
                        add(Artwork.fromCursor(cursor))

                        cursor.moveToNext()
                    }
                }
            } ?: emptyList()

        // TODO: Paging
        val artworks = lightroom
            .getAlbumAssets(albumId)
            // Drop items already added to Muzei
            .filterNot { albumAsset ->
                previouslyAddedAssets.any { asset ->
                    asset.token == albumAsset.id.id
                }
            }
            .map { asset ->
                fun LocalDateTime.format(): String = "$dayOfMonth ${month.name} $year"

                Artwork(
                    title = "${album.name} - ${asset.captureDate.format()}",
                    byline = "${asset.cameraBody}, ${asset.lens}",
                    attribution = "ISO ${asset.iso} - ${asset.focalLength} - ${asset.aperture} - ${asset.shutterSpeed}",
                    token = asset.id.id,
                    persistentUri = with(lightroom) {
                        asset.id.asUrl(rendition = Rendition.Full).toUri()
                    },
                )
            }

        albumProvider.addArtwork(artworks)

        return Result.success()
    }
}
