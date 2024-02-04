// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.lib.search

import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Asset

internal class LightroomSearchUseCase(private val lightroom: Lightroom) : SearchUseCase {
    override suspend fun invoke(config: SearchConfig): Result<List<Asset>> {
        return runCatching { lightroom.loadAssets(config) }
    }
}

/**
 * Create a search use-case from the receiving Lightroom instance.
 */
fun Lightroom.searchUseCase(): SearchUseCase {
    return LightroomSearchUseCase(this)
}

/**
 * Extension function for loading all relevant Assets from Lightroom
 * given a [searchConfig]. This function is responsible for selecting the source of the artwork,
 * as well as formatting Artwork-related information into something user-visible in Muzei.
 *
 * @receiver Lightroom instance
 * @param searchConfig Config to use when loading artwork
 * @return List of artwork matching [searchConfig]
 */
private suspend fun Lightroom.loadAssets(searchConfig: SearchConfig): List<Asset> {
    val assets =
        when (val source = searchConfig.source) {
            is SearchConfig.Source.Album ->
                getAlbumAssets(source.requireId())

            is SearchConfig.Source.Catalog ->
                getCatalogAssets()
        }

    return assets.filter { searchConfig.permitsAsset(it) }
}
