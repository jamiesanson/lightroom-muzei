package dev.sanson.lightroom.data.filter

import androidx.datastore.core.DataStore
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FilterRepository {
    val filter: Flow<Filter?>

    suspend fun addKeyword(keyword: String)
    suspend fun removeKeyword(keyword: String)
    suspend fun setRatingRange(start: Int, end: Int = start)
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

    override suspend fun setRatingRange(start: Int, end: Int) {
        filterStore.updateData {
            it?.copy(
                rating = IntRange(
                    start = start,
                    endInclusive = end,
                ),
            )
        }
    }

    override suspend fun updateFlag(flag: Asset.Flag?) {
        filterStore.updateData { it?.copy(review = flag) }
    }
}
