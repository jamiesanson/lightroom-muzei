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
import dev.sanson.lightroom.common.config.Config
import dev.sanson.lightroom.sdk.Lightroom
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker, which coordinates loading the selected album and populating
 * the Muzei [ProviderClient].
 */
@HiltWorker
class LoadArtWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val lightroom: Lightroom,
        private val configStore: DataStore<Config?>,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val config = configStore.data.first() ?: return Result.failure()
            val albumProvider =
                getProviderClient<LightroomArtProvider>(context = applicationContext)

            val previouslyAddedAssets =
                albumProvider
                    .getArtwork(
                        contentResolver = applicationContext.contentResolver,
                    ).mapNotNull { it.token }

            val artworks =
                lightroom
                    .loadAssets(config)
                    .map { it.toArtwork() }
                    .filterNot { albumAsset ->
                        albumAsset.token in previouslyAddedAssets
                    }

            albumProvider.addArtwork(artworks)

            return Result.success()
        }
    }

/**
 * Query [ProviderClient] using [contentResolver] for all existing artwork
 */
private fun ProviderClient.getArtwork(contentResolver: ContentResolver): List<Artwork> =
    contentResolver
        .query(contentUri, null, null, null, null)
        ?.use { cursor ->
            cursor.moveToFirst()

            buildList {
                while (!cursor.isAfterLast) {
                    add(Artwork.fromCursor(cursor))

                    cursor.moveToNext()
                }
            }
        } ?: emptyList()
