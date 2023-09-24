package dev.sanson.lightroom.data.filter

import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.AssetId
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test

class FilterTest {

    /**
     * An asset with no user-defined tags, such as rating, keywords or flags
     */
    private val untaggedAsset = Asset(
        id = AssetId("2"),
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
    fun `Empty Filter permits all assets`() = runTest {
        val filter = Filter(albumId = AlbumId("1"))

        forAll(listOf(untaggedAsset, pickedAsset, rejectedAsset).exhaustive()) {
            filter.permitsAsset(it)
        }
    }

    @Test
    fun `Filter with picked condition rejects all other asset`() {
        val filter = Filter(albumId = AlbumId("1"), review = Asset.Flag.Picked)

        with(filter) {
            permitsAsset(rejectedAsset) shouldBe false
            permitsAsset(untaggedAsset) shouldBe false
        }
    }

    @Test
    fun `Filter with rating specification rejects outlying assets`() = runTest {
        val filter = Filter(albumId = AlbumId("1"), rating = 3..5)

        forAll(Arb.int(min = 0, max = 5)) { rating ->
            val accepted = filter.permitsAsset(untaggedAsset.copy(rating = rating))

            accepted == (rating >= 3)
        }
    }

    @Test
    fun `Filter with keywords rejects unmatching assets`() = runTest {
        val filter = Filter(albumId = AlbumId("1"), keywords = setOf("wallpaper"))

        forAll(Arb.string()) { keyword ->
            val accepted = filter.permitsAsset(untaggedAsset.copy(keywords = listOf(keyword)))

            accepted == (keyword == "wallpaper")
        }
    }
}
