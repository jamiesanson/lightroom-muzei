package dev.sanson.lightroom.muzei

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
import dev.sanson.lightroom.data.config.Config
import dev.sanson.lightroom.data.config.permitsAsset
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
    private val configStore: DataStore<Config?>,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val config = configStore.data.first() ?: return Result.failure()
        val albumProvider = getProviderClient<LightroomAlbumProvider>(context = applicationContext)

        val previouslyAddedAssets = albumProvider.getArtwork(
            contentResolver = applicationContext.contentResolver,
        ).mapNotNull { it.token }

        val artworks = loadAssets(config.source)
            .filter { config.permitsAsset(it) }
            .filterNot { albumAsset ->
                albumAsset.id.id in previouslyAddedAssets
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
     * title = Album Name - Date (London - 9 Nov 2022)
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
            title = captureDate.toLocalDateTime(TimeZone.currentSystemDefault()).format(),
            byline = "$cameraBody, $lens",
            attribution = "ISO $iso - $focalLength - $aperture - $shutterSpeed",
            token = id.id,
            persistentUri = with(lightroom) {
                id.asUrl(rendition = Rendition.Full).toUri()
            },
            webUri = "https://lightroom.adobe.com/libraries/${catalogId.id}/assets/${id.id}".toUri(),
            metadata = catalogId.id,
        )
    }

    private suspend fun loadAssets(source: Config.Source): List<Asset> {
        return when (source) {
            is Config.Source.Album ->
                lightroom.getAlbumAssets(source.id)

            is Config.Source.Catalog ->
                lightroom.getCatalogAssets()
        }
    }
}
