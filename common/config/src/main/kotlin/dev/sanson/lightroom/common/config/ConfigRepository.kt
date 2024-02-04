// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.common.config

import androidx.datastore.core.DataStore
import dev.sanson.lightroom.lib.search.SearchConfig
import dev.sanson.lightroom.sdk.model.AlbumId
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    val searchConfig: Flow<SearchConfig?>

    suspend fun updateConfig(searchConfig: SearchConfig)

    suspend fun setAlbum(albumId: AlbumId)

    suspend fun setImageSource(imageSource: SearchConfig.Source)
}

internal class DefaultConfigRepository(
    private val searchConfigStore: DataStore<SearchConfig?>,
) : ConfigRepository {
    override val searchConfig: Flow<SearchConfig?> get() = searchConfigStore.data

    override suspend fun updateConfig(searchConfig: SearchConfig) {
        searchConfigStore.updateData { searchConfig }
    }

    override suspend fun setAlbum(albumId: AlbumId) {
        searchConfigStore.updateData {
            val source = SearchConfig.Source.Album(id = albumId)

            it?.copy(source = source) ?: SearchConfig(source = source)
        }
    }

    override suspend fun setImageSource(imageSource: SearchConfig.Source) {
        searchConfigStore.updateData { currentConfig ->
            when {
                currentConfig == null ->
                    SearchConfig(source = imageSource)

                currentConfig.source::class != imageSource::class -> {
                    currentConfig.copy(source = imageSource)
                }

                else ->
                    currentConfig
            }
        }
    }
}
