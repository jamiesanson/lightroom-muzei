// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.core.config

import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Asset

/**
 * Extension function for loading all relevant Muzei [Artwork] from Lightroom
 * given a [config]. This function is responsible for selecting the source of the artwork,
 * as well as formatting Artwork-related information into something user-visible in Muzei.
 *
 * @receiver Lightroom instance
 * @param config Config to use when loading artwork
 * @return List of artwork matching [config]
 */
suspend fun Lightroom.loadAssets(config: Config): List<Asset> {
    val assets =
        when (val source = config.source) {
            is Config.Source.Album ->
                getAlbumAssets(source.requireId())

            is Config.Source.Catalog ->
                getCatalogAssets()
        }

    return assets.filter { config.permitsAsset(it) }
}
