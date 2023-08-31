package dev.sanson.lightroom.data.filter

import androidx.datastore.core.DataStore
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FilterRepository {
    val filter: Flow<Filter?>

    suspend fun addKeyword(keyword: String)
    suspend fun removeKeyword(keyword: String)
    suspend fun setRating(rating: Int)
    suspend fun setRatingUpToMax(upToMax: Boolean)
    suspend fun updateFlag(flag: Asset.Flag?)
}

class DefaultFilterRepository @Inject constructor(
    private val filterStore: DataStore<Filter?>,
) : FilterRepository {
    override val filter: Flow<Filter?> get() = filterStore.data

    override suspend fun addKeyword(keyword: String) {
        filterStore.updateData { it?.copy(keywords = it.keywords + keyword) }
    }

    override suspend fun removeKeyword(keyword: String) {
        filterStore.updateData { it?.copy(keywords = it.keywords - keyword) }
    }

    override suspend fun setRating(rating: Int) {
        filterStore.updateData {
            it?.copy(
                rating = IntRange(
                    start = rating,
                    endInclusive = it.rating?.endInclusive ?: rating,
                ),
            )
        }
    }

    override suspend fun setRatingUpToMax(upToMax: Boolean) {
        filterStore.updateData {
            val start = it?.rating?.first ?: 0
            it?.copy(
                rating = IntRange(
                    start = start,
                    endInclusive = if (upToMax) 5 else start,
                ),
            )
        }
    }

    override suspend fun updateFlag(flag: Asset.Flag?) {
        filterStore.updateData { it?.copy(review = flag) }
    }
}
