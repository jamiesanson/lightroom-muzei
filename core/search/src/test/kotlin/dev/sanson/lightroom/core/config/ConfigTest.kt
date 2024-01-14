// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.core.config

import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.CatalogId
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test

class ConfigTest {
    /**
     * An asset with no user-defined tags, such as rating, keywords or flags
     */
    private val untaggedAsset =
        Asset(
            id = AssetId("2"),
            catalogId = CatalogId("1"),
            captureDate = Instant.DISTANT_FUTURE,
            cameraBody = "Fujifilm X-T3",
            lens = "XF 18mm f/1.4",
            iso = 80,
            shutterSpeed = "1/500",
            aperture = "f/3.2",
            focalLength = "18mm",
            keywords = emptyList(),
        )

    private val pickedAsset = untaggedAsset.copy(review = Asset.Flag.Picked)
    private val rejectedAsset = untaggedAsset.copy(review = Asset.Flag.Rejected)

    @Test
    fun `Empty Config permits all assets`() =
        runTest {
            val config = Config(source = Config.Source.Catalog)

            forAll(listOf(untaggedAsset, pickedAsset, rejectedAsset).exhaustive()) {
                config.permitsAsset(it)
            }
        }

    @Test
    fun `Config with picked condition rejects all other asset`() {
        val config = Config(source = Config.Source.Catalog, review = Asset.Flag.Picked)

        with(config) {
            permitsAsset(rejectedAsset) shouldBe false
            permitsAsset(untaggedAsset) shouldBe false
        }
    }

    @Test
    fun `Config with rating specification rejects outlying assets`() =
        runTest {
            val config = Config(source = Config.Source.Catalog, rating = 3..5)

            forAll(Arb.int(min = 0, max = 5)) { rating ->
                val accepted = config.permitsAsset(untaggedAsset.copy(rating = rating))

                accepted == (rating >= 3)
            }
        }

    @Test
    fun `Config with keywords rejects unmatching assets`() =
        runTest {
            val config = Config(source = Config.Source.Catalog, keywords = setOf("wallpaper"))

            forAll(Arb.string()) { keyword ->
                val accepted = config.permitsAsset(untaggedAsset.copy(keywords = listOf(keyword)))

                accepted == (keyword == "wallpaper")
            }
        }
}
