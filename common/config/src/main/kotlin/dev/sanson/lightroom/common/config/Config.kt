// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.common.config

import dev.sanson.lightroom.common.config.serializer.IntRangeSerializer
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.serialization.Serializable

/**
 * Model representing a filter to apply to a collection of assets, used when populating images
 * for Muzei.
 *
 * @param source        The source of the collection of assets
 * @param keywords      Keywords to filter the set of assets by
 * @param rating        IntRange describing the rating of images to include
 * @param review        Select picked (or rejected) assets
 * @param serialVersion Version used to ease future migrations
 */
@Serializable
data class Config(
    val source: Source,
    val keywords: Set<String> = emptySet(),
    @Serializable(with = IntRangeSerializer::class)
    val rating: IntRange? = null,
    val review: Asset.Flag? = null,
    val serialVersion: Int = 1,
) {
    sealed interface Source {
        @Serializable
        data class Album(
            val id: AlbumId?,
        ) : Source {
            fun requireId(): AlbumId = requireNotNull(id)

            companion object {
                val Uninitialized = Album(id = null)
            }
        }

        @Serializable
        data object Catalog : Source
    }
}

fun Config.permitsAsset(asset: Asset): Boolean =
    when {
        // Has matching keyword
        keywords.isNotEmpty() && asset.keywords.none { it in keywords } -> false

        // Has rating within bounds
        rating != null && asset.rating !in rating -> false

        // Has correct flag
        review != null && asset.review != review -> false

        else -> true
    }
