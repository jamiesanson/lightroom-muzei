package dev.sanson.lightroom.data

import android.content.Context
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

        val artworks = lightroom
            .getAlbumAssets(albumId)
            // Drop items already added to Muzei
            .filterNot { albumAsset ->
                previouslyAddedAssets.any { asset ->
                    asset.token == albumAsset.id.id
                }
            }
            .map<_, Artwork> {
                /*
                title = "Album Name - Date"
                byline = "Camera & lens (Fujifilm X-T3, XF16-55mm etc.)
                attribution = "Capture specs (ISO 160 55mm f/4.0 1/160s)"
                token = <asset_id>
                persistentUrl = <asset_id URI>
                webUrl = <lightroom deeplink??>
                metadata = <catalog_id>
                 */
                TODO("Construct Artwork")
            }

        albumProvider.addArtwork(artworks)

        return Result.success()
    }
}
