// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei

import android.content.ContentResolver
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
import dev.sanson.lightroom.lib.search.SearchConfig
import dev.sanson.lightroom.lib.search.loadAssets
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.backend.auth.Credential
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.search.api.SearchRequest
import dev.sanson.lightroom.search.api.SearchService
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker, which coordinates loading the selected album and populating
 * the Muzei [ProviderClient].
 */
@HiltWorker
class LoadArtWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val lightroom: Lightroom,
    private val searchService: SearchService,
    private val searchConfigStore: DataStore<SearchConfig?>,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val config = searchConfigStore.data.first() ?: return Result.failure()
        val albumProvider =
            getProviderClient<LightroomArtProvider>(context = applicationContext)

        val previouslyAddedAssets =
            albumProvider.getArtwork(
                contentResolver = applicationContext.contentResolver,
            ).mapNotNull { it.token }

        val artworks =
            search(config)
                .getOrElse { return Result.failure() }
                .filterNot { albumAsset ->
                    albumAsset.token in previouslyAddedAssets
                }

        albumProvider.addArtwork(artworks)

        return Result.success()
    }

    private suspend fun search(config: SearchConfig): kotlin.Result<List<Artwork>> {
        // TODO: Remove the credentials from here, have an interceptor add a header
        return runCatching { loadApiAssets(lightroom.authManager.refreshTokens(), config) }
            .recoverCatching { lightroom.loadAssets(config) }
            .map { assets ->
                assets.map { it.toArtwork() }
            }
    }

    private suspend fun loadApiAssets(
        credential: Credential,
        config: SearchConfig,
    ): List<Asset> {
        return searchService.search(SearchRequest(credential, config)).assets
    }
}

/**
 * Query [ProviderClient] using [contentResolver] for all existing artwork
 */
private fun ProviderClient.getArtwork(contentResolver: ContentResolver): List<Artwork> {
    return contentResolver.query(contentUri, null, null, null, null)
        ?.use { cursor ->
            cursor.moveToFirst()

            buildList {
                while (!cursor.isAfterLast) {
                    add(Artwork.fromCursor(cursor))

                    cursor.moveToNext()
                }
            }
        } ?: emptyList()
}
