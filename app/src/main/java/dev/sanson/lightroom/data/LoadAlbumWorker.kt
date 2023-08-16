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
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

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
            .map { asset -> asset.toArtwork() }

        albumProvider.addArtwork(artworks)

        return Result.success()
    }

    /**
     * Map [Asset] to [Artwork]
     *
     * The expected format is as such:
     *
     * title = Album Name - Date (London - 23 Nov 2022)
     * byline = Camera & lens (Fujifilm X-T3, XF16-55mm etc.)
     * attribution = Capture specs (ISO 160 55mm f/4.0 1/160s)
     * token = <asset_id>
     * persistentUrl = <asset_id URI>
     * webUrl = <lightroom web URL> (https://lightroom.adobe.com/libraries/<catalog_id>/assets/<asset_id>)
     * metadata = <catalog_id>
     */
    private suspend fun Asset.toArtwork(): Artwork {
        fun LocalDateTime.format(): String =
            "$dayOfMonth ${
                month.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault(),
                )
            } $year"

        val catalogId = lightroom.getCatalog().id

        return Artwork(
            title = captureDate.format(),
            byline = "$cameraBody, $lens",
            attribution = "ISO $iso - $focalLength - $aperture - $shutterSpeed",
            token = id.id,
            persistentUri = with(lightroom) {
                id.asUrl(rendition = Rendition.Full).toUri()
            },
            webUri = "https://lightroom.adobe.com/libraries/${catalogId.id}/assets/${id.id}".toUri(),
        )
    }
}
