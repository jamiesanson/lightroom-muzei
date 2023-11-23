package dev.sanson.lightroom.common.config

import androidx.datastore.core.DataStore
import dev.sanson.lightroom.sdk.model.AlbumId
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    val config: Flow<Config?>

    suspend fun updateConfig(config: Config)

    suspend fun setAlbum(albumId: AlbumId)

    suspend fun setImageSource(imageSource: Config.Source)
}

internal class DefaultConfigRepository(
    private val configStore: DataStore<Config?>,
) : ConfigRepository {
    override val config: Flow<Config?> get() = configStore.data

    override suspend fun updateConfig(config: Config) {
        configStore.updateData { config }
    }

    override suspend fun setAlbum(albumId: AlbumId) {
        configStore.updateData {
            val source = Config.Source.Album(id = albumId)

            it?.copy(source = source) ?: Config(source = source)
        }
    }

    override suspend fun setImageSource(imageSource: Config.Source) {
        configStore.updateData { currentConfig ->
            when {
                currentConfig == null ->
                    Config(source = imageSource)

                currentConfig.source::class != imageSource::class -> {
                    currentConfig.copy(source = imageSource)
                }

                else ->
                    currentConfig
            }
        }
    }
}
