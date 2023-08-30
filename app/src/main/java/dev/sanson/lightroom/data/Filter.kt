package dev.sanson.lightroom.data

import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.serialization.Serializable

/**
 * Model representing a filter to apply to a collection of assets, used when populating images
 * for Muzei.
 *
 * @param albumId       The ID of the album to access.
 *                          Note: one possible "improvement" could be to make this optional, and filter the entire
 *                          catalog worth of assets client-side. This would certainly mean more flexibility in
 *                          assets chosen, but in cases of large catalogs would mean more thrashing of the API
 *                          than I'd be comfortable with.
 * @param keywords      Keywords to filter the set of assets by
 * @param rating        IntRange describing the rating of images to include
 * @param review        Select picked (or rejected) assets
 * @param serialVersion Version used to ease future migrations
 */
@Serializable
data class Filter(
    val albumId: AlbumId,
    val keywords: List<String> = emptyList(),
    @Serializable(with = IntRangeSerializer::class)
    val rating: IntRange? = null,
    val review: Asset.Flag? = null,
    val serialVersion: Int = 1,
)
