package dev.sanson.lightroom.common.config

import androidx.datastore.core.DataStore
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    val config: Flow<Config?>

    suspend fun addKeyword(keyword: String)

    suspend fun removeKeyword(keyword: String)

    suspend fun setRatingRange(
        start: Int,
        end: Int = start,
    )

    suspend fun updateFlag(flag: Asset.Flag?)

    suspend fun setAlbum(albumId: AlbumId)

    suspend fun setImageSource(imageSource: Config.Source)
}

class DefaultConfigRepository(
    private val configStore: DataStore<Config?>,
) : ConfigRepository {
    override val config: Flow<Config?> get() = configStore.data

    override suspend fun addKeyword(keyword: String) {
        configStore.updateData { it?.copy(keywords = it.keywords + keyword) }
    }

    override suspend fun removeKeyword(keyword: String) {
        configStore.updateData { it?.copy(keywords = it.keywords - keyword) }
    }

    override suspend fun setRatingRange(
        start: Int,
        end: Int,
    ) {
        configStore.updateData {
            it?.copy(
                rating =
                    IntRange(
                        start = start,
                        endInclusive = end,
                    ),
            )
        }
    }

    override suspend fun updateFlag(flag: Asset.Flag?) {
        configStore.updateData { it?.copy(review = flag) }
    }

    override suspend fun setAlbum(albumId: AlbumId) {
        configStore.updateData {
            val source = Config.Source.Album(id = albumId)

            it?.copy(source = source) ?: Config(source = source)
        }
    }

    override suspend fun setImageSource(imageSource: Config.Source) {
        configStore.updateData {
            it?.copy(source = imageSource) ?: Config(source = imageSource)
        }
    }
}
